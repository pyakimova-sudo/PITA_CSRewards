package com.example.pita_rewards2.userActivities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pita_rewards2.checkoutActivities.BasketActivity
import com.example.pita_rewards2.R
import com.example.pita_rewards2.mainActivities.LoginActivity
import com.example.pita_rewards2.mainActivities.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Account : AppCompatActivity() {

    private lateinit var navigation: BottomNavigationView
    private lateinit var passwordChange: TextView
    private lateinit var phoneChange: TextView
    private lateinit var userId: String  // store current user id
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        // Handle edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.account)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        // SharedPreferences to persist userId
        prefs = getSharedPreferences("PitaPrefs", MODE_PRIVATE)

        // Get userId from Intent, fallback to SharedPreferences
        userId = intent.getStringExtra("userId") ?: prefs.getString("userId", "") ?: ""

        // Save userId to SharedPreferences if it came from Intent
        if (userId.isNotEmpty()) {
            prefs.edit().putString("userId", userId).apply()
        }

        // No redirect to login — just show a toast if userId is empty
        if (userId.isEmpty()) {
            Toast.makeText(this, "User ID not found. Some features may not work.", Toast.LENGTH_LONG).show()
        }

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

        val logoutButton = findViewById<Button>(R.id.logout_button)

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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
}