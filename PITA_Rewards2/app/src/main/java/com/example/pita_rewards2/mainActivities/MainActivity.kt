package com.example.pita_rewards2.mainActivities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.ComponentActivity
import com.example.pita_rewards2.databinding.ActivityMainBinding
import com.google.firebase.database.*
import android.widget.Spinner
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pita_rewards2.R
import com.example.pita_rewards2.userActivities.Account
import com.example.pita_rewards2.checkoutActivities.BasketActivity
import com.example.pita_rewards2.userActivities.UserData

object DisabledButtons {
    private val disabledSet = mutableSetOf<String>()

    fun setDisabled(tag: String, disabled: Boolean) {
        if (disabled) disabledSet.add(tag)
        else disabledSet.remove(tag)
    }

    fun isDisabled(tag: String) = disabledSet.contains(tag)
}

class MainActivity : ComponentActivity(), AdapterClass.RecyclerViewEvent {
    private lateinit var recyclerView: RecyclerView
    private lateinit var drinkMenu: ArrayList<Drink_Menu>
    private lateinit var adapter: AdapterClass
    lateinit var imageList:Array<Int>
    lateinit var nameList:Array<String>
    lateinit var priceList:Array<Int>
    private lateinit var binding: ActivityMainBinding
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

        imageList = arrayOf(
            R.drawable.latte, R.drawable.mocha, R.drawable.smoothie,
            R.drawable.matcha, R.drawable.cold_brew,
            R.drawable.water, R.drawable.lemonade,
            R.drawable.tea, R.drawable.hot_chocolate,
            R.drawable.milk
        )

        nameList = arrayOf(
            "Latte", "Mocha","Smoothie", "Matcha", "Cold Brew",
            "Water", "Lemonade", "Tea", "Hot Chocolate",
            "Milk"
        )

        priceList = arrayOf(
            5,5, 3, 5, 5, 1, 3, 3, 5, 3
        )

        recyclerView = findViewById(R.id.menu_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(false)

        drinkMenu = arrayListOf<Drink_Menu>()
        getData()



        val weeklyDeal = findViewById<ImageView>(R.id.weekly_image)
        weeklyDeal.setImageResource(imageList[3])

        val userRef = FirebaseDatabase.getInstance().getReference("users")
        //extract userID after login
        val userId = intent.getStringExtra("userId")
        val userText: TextView = findViewById(R.id.user)
        //If valid user adds first name to welcome
        if (userId != null) {
            userRef.child(userId).get().addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(UserData::class.java)
                userText.text = "Welcome ${user?.firstName}"
            }
        } else{
            Toast.makeText(this, "No User Id", Toast.LENGTH_SHORT).show()
        }

        val spinner: Spinner = findViewById(R.id.location_dropdown)
        ArrayAdapter.createFromResource(
            this, R.array.locations, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        navigation = findViewById(R.id.bottom_navigation)

        navigation.selectedItemId = R.id.home

        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.account -> {
                    val intent = Intent(this, Account::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("userId", userId)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.basket -> {
                    val intent = Intent(this, BasketActivity::class.java)
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
        drinkMenu.clear()
        for (i in imageList.indices){
            val menu = Drink_Menu(nameList[i], priceList[i], imageList[i])
            drinkMenu.add(menu)
        }
        adapter = AdapterClass(drinkMenu, this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        val drink = drinkMenu[position]
        if (DisabledButtons.isDisabled(drink.name)) {
            Toast.makeText(this, "${drink.name} is currently unavailable", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = intent.getStringExtra("userId")

        val intent = Intent(this, Drink_Customization::class.java)
        intent.putExtra("selected_drink", drink)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }
}

val drinksRef = FirebaseDatabase.getInstance().getReference("drinks")
// Function to add or update a user
fun addItem(drink: Drink_Menu) {
    fun addItem(drink: Drink_Menu): String? {
        val drinkName = drink.name.ifEmpty { drinksRef.push().key } ?: return null
        drink.name = drinkName

        drinksRef.child(drinkName).setValue(drink)
        return drinkName
    }
}

fun removeDrink(userId: String) {
    drinksRef.child(userId).removeValue()
}


