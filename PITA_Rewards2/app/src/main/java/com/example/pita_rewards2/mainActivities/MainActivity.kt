package com.example.pita_rewards2.mainActivities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.pita_rewards2.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pita_rewards2.userActivities.Account
import com.example.pita_rewards2.checkoutActivities.BasketActivity
import com.example.pita_rewards2.R
import com.example.pita_rewards2.userActivities.UserData

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drinkMenu: ArrayList<Drink_Menu>
    lateinit var navigation: BottomNavigationView

    companion object {
        val order: MutableList<String> = mutableListOf()
        val customizations: MutableList<ItemCustomization> = mutableListOf()
        var isOrderSubmitted = false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve userId from intent
        val userId = intent.getStringExtra("userId")

        val recyclerView = binding.menuRecycler
        recyclerView.layoutManager = LinearLayoutManager(this)

        drinkMenu = ArrayList(Drink_Menu.defaultDrinks)

        recyclerView.adapter = AdapterClass(drinkMenu, userId)

        if (userId != null) {
            // User is logged in, fetch and display user data
            val userRef = FirebaseDatabase.getInstance().getReference("users")
            val userText: TextView = findViewById(R.id.user)
            userRef.child(userId).get().addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(UserData::class.java)
                userText.text = "Welcome ${user?.firstName}"
            }
        } else {
            // If userId is missing, redirect to login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }


        // Bottom navigation setup
        navigation = findViewById(R.id.bottom_navigation)
        navigation.selectedItemId = R.id.home

        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.account -> {
                    // Pass userId to AccountActivity
                    val intent = Intent(this, Account::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.basket -> {
                    // Pass userId to BasketActivity
                    val intent = Intent(this, BasketActivity::class.java)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> false
            }
        }
    }
}


object DisabledButtons {
    private val disabledSet = mutableSetOf<String>()

    fun setDisabled(tag: String, disabled: Boolean) {
        if (disabled) disabledSet.add(tag)
        else disabledSet.remove(tag)
    }
    fun isDisabled(tag: String) = disabledSet.contains(tag)
}