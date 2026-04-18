package com.example.pita_rewards2.userActivities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pita_rewards2.checkoutActivities.BasketActivity
import com.example.pita_rewards2.R
import com.example.pita_rewards2.mainActivities.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.concurrent.timer

class Account : AppCompatActivity() {

    private lateinit var navigation: BottomNavigationView
    private lateinit var passwordChange: TextView
    private lateinit var phoneChange: TextView
    private lateinit var userId: String  // store current user id
    private lateinit var prefs: SharedPreferences
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

        val points = intent.getStringExtra("points")

        userId = intent.getStringExtra("userId") ?: prefs.getString("userId", "") ?: ""

        if (userId.isNotEmpty()) {
            prefs.edit().putString("userId", userId).apply()
        }

        //User Id test
        if (userId.isEmpty()) {
            Toast.makeText(this, "User ID not found. Some features may not work.", Toast.LENGTH_LONG).show()
        }

        drink_timer = findViewById(R.id.drink_timer)
        //to stop time reset
        Timer_fix()

        navigation = findViewById(R.id.bottom_navigation)
        navigation.selectedItemId = R.id.account

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

        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("userId",userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.basket -> {
                    val intent = Intent(this, BasketActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("userId",userId)
                    intent.putExtra("points", points)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.account -> {
                    // Already on account — do nothing
                    true
                }
                else -> false
            }
        }
    }
    private fun Timer_fix(){
        //Catch for no orders
        val totalQuantity = try {
            MainActivity.customizations.sumOf { it.quantity }
        } catch (e: Exception) {
            0
        }
        if (totalQuantity <= 0) {
            drink_timer.text = "No active drinks"
            prefs.edit().remove("drink_timer_end").apply()
            prefs.edit().remove("drink_timer_end").apply()
            return
        }

        val duration = 120_000L * totalQuantity

        val savedEndTime = prefs.getLong("drink_timer_end", 0L)
        val now = System.currentTimeMillis()

        val endTime = if (savedEndTime > now) {
            savedEndTime // continue existing timer
        } else {
            // create new timer
            val newEnd = now + duration
            prefs.edit().putLong("drink_timer_end", newEnd).apply()
            newEnd
        }
        val remaining = endTime - now
        if (remaining <= 0) {
            drink_timer.text = "Done!"
            return
        }

        timer?.cancel()

        timer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                drink_timer.text = "Seconds remaining: ${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                drink_timer.text = "Done!"
                prefs.edit().remove("drink_timer_end").apply()
            }
        }

        timer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}

/*
binding.logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        binding.drinkTimer.setOnClickListener {
            val intent = Intent(this, TimerPopUp::class.java)
            startActivity(intent)
        }
 */