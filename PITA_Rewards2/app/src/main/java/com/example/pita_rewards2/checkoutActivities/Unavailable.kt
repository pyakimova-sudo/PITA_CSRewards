package com.example.pita_rewards2.checkoutActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pita_rewards2.R
import com.example.pita_rewards2.databinding.ActivityUnavailableBinding
import com.example.pita_rewards2.mainActivities.Drink_Menu
import com.example.pita_rewards2.mainActivities.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class Unavailable : AppCompatActivity() {
    private lateinit var drinkMenu: ArrayList<Drink_Menu>
    private lateinit var adapter: UnavailableAdapter
    lateinit var navigation: BottomNavigationView
    private lateinit var binding: ActivityUnavailableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnavailableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("userId")

        binding.itemList.layoutManager = LinearLayoutManager(this)
        binding.itemList.setHasFixedSize(false)

        drinkMenu = arrayListOf<Drink_Menu>()

        getData()

        binding.menu.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val menuButton = findViewById<Button>(R.id.menu)
        menuButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        navigation = findViewById(R.id.bottom_navigation)
        navigation.selectedItemId = R.id.unavailability

        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.orders -> {
                    val intent = Intent(this, EmployeeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("userId", userId)
                    startActivity(intent)

                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun getData(){
        val imageList = arrayOf(
            R.drawable.latte, R.drawable.mocha, R.drawable.smoothie,
            R.drawable.matcha, R.drawable.cold_brew,
            R.drawable.water, R.drawable.lemonade,
            R.drawable.tea, R.drawable.hot_chocolate,
            R.drawable.milk
        )

        val nameList = arrayOf(
            "Latte", "Mocha","Smoothie", "Matcha", "Cold Brew",
            "Water", "Lemonade", "Tea", "Hot Chocolate",
            "Milk"
        )
        drinkMenu.clear()
        for (i in imageList.indices){
            drinkMenu.add(Drink_Menu(name=nameList[i], image =  imageList[i]))
        }
        adapter = UnavailableAdapter(drinkMenu, this)
        binding.itemList.adapter = adapter
    }
}