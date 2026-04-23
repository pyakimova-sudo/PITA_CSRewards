package com.example.pita_rewards2.userActivities
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pita_rewards2.checkoutActivities.BasketActivity
import com.example.pita_rewards2.R
import com.example.pita_rewards2.checkoutActivities.EmployeeActivity
import com.example.pita_rewards2.mainActivities.LoginActivity
import com.example.pita_rewards2.mainActivities.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

//Needs to be global for use
//TODO make total orders var in checkout using total items
//TODO then use total to calculate order time

class Account : AppCompatActivity() {

    private lateinit var navigation: BottomNavigationView
    private lateinit var passwordChange: TextView
    private lateinit var phoneChange: TextView
    private lateinit var userId: String
    private lateinit var customerName: String
    private lateinit var prefs: SharedPreferences
    private var totalQuantity: Int = 0
    private var timer: CountDownTimer? = null
    private lateinit var drink_timer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        //Handle edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.account)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        //SharedPreferences to persist userId
        prefs = getSharedPreferences("PitaPrefs", MODE_PRIVATE)

        val logoutButton = findViewById<TextView>(R.id.logout_button)
        logoutButton.setOnClickListener {
            prefs.edit().clear().apply()
            //Button to logout
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra("userId", userId)
            intent.putExtra("totalQuantity", totalQuantity)
            startActivity(intent)
            finish()
        }

        //TODO: use points right
        //val points = intent.getStringExtra("points")

        userId = intent.getStringExtra("userId") ?: prefs.getString("userId", "") ?: ""
        customerName = intent.getStringExtra("customerName") ?: prefs.getString("customerName", "Customer") ?: "Customer"

        if (userId.isNotEmpty()) {
            prefs.edit().putString("userId", userId).apply()
        }else{
            Toast.makeText(this, "User ID not found. Some features may not work.", Toast.LENGTH_LONG).show()
        }

        drink_timer = findViewById(R.id.drink_timer)

        navigation = findViewById(R.id.bottom_navigation)
        navigation.selectedItemId = R.id.account

        //Countdown Timer
        val endTime = prefs.getLong("drink_timer_end", 0L)
        val isOrderActive = prefs.getBoolean("is_order_active", false)
        //Start countdown only after order
        if (isOrderActive && endTime > System.currentTimeMillis()) {
            startCountdown(endTime)
        } else {
            drink_timer.text = "No active drinks"
        }

        //Quantity of order(unfinished)
        totalQuantity = intent.getIntExtra("totalQuantity", 0)

        navigation = findViewById(R.id.bottom_navigation)
        navigation.selectedItemId = R.id.account

        //Change password
        passwordChange = findViewById(R.id.passwordChange)
        passwordChange.setOnClickListener {
            val savedUserId = prefs.getString("userId", "")
            if (savedUserId.isNullOrEmpty()) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, ChangePassword::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        //Edit phone number
        phoneChange = findViewById(R.id.phoneChange)
        phoneChange.setOnClickListener {
            val savedUserId = prefs.getString("userId", "")
            if (savedUserId.isNullOrEmpty()) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, ChangePhone::class.java)
            intent.putExtra("userId", savedUserId)
            startActivity(intent)
        }

        //Activity navigation
        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("userId", userId)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.basket -> {
                    val intent = Intent(this, BasketActivity::class.java)
                    intent.putExtra("userId", userId)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.account -> true
                else -> false
            }
        }
    }

    //Local timer(only tracks current order(fills space))
    private fun startCountdown(endTime: Long) {
        timer?.cancel()

        val remaining = endTime - System.currentTimeMillis()

        if (remaining <= 0) {
            drink_timer.text = "Done!"
            return
        }
        timer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(ms: Long) {
                val totalSeconds = ms / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                drink_timer.text = String.format("%02d:%02d remaining", minutes, seconds)
            }
            override fun onFinish() {
                drink_timer.text = "Done!"
                prefs.edit()
                    .remove("drink_timer_end")
                    .putBoolean("is_order_active", false)
                    .apply()

                sendNotification(userId, customerName)
            }
        }.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
    //For use after timer countdown
    private fun sendNotification(userId: String, customerName: String) {

        val channelId = "order_channel"

        val intent = Intent(this@Account, MainActivity::class.java)
        intent.putExtra("userId", userId)

        val pendingIntent = PendingIntent.getActivity(
            this@Account,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this@Account, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Order Ready")
            .setContentText("$customerName, your drink is ready!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val manager = NotificationManagerCompat.from(this@Account)
        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}