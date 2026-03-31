package com.example.pita_rewards2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.pita_rewards2.databinding.ActivityMainBinding
import com.google.firebase.database.*
import android.widget.Spinner
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import android.widget.TextView

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var navigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Fluid button mapping for all Drink_Menu items
        /*
        TODO conditionally divide drinks by type
         to better organize menu screen(aestetics)*/
        val buttonContainer = binding.drinkButtonContainer
        Drink_Menu.defaultDrinks.forEach { drink ->
            val button = Button(this).apply {
                text = drink.name
                //TODO:UI things here(picture of drink)
                //Could maybe add picture to data class for
                //repeated use??
                textSize = 18f
                setOnClickListener {
                    val intent = Intent(this@MainActivity, Drink_Customization::class.java)
                    intent.putExtra("selected_drink", drink)
                    startActivity(intent)
                }
            }
            buttonContainer.addView(button)
        }
/*
        //Button for latte customization
        val coffeeButton: Button? = findViewById(R.id.squareButton)
        coffeeButton?.setOnClickListener {
            val latte = Drink_Menu.defaultDrinks.first { it.name == "Latte" }
            // Launch DrinkCustomization
            val intent = Intent(this, Drink_Customization::class.java)
            //Places data from latte into Drink_Customization
            intent.putExtra("selected_drink", latte)
            startActivity(intent)
        }
*/
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
        }

        val spinner: Spinner = findViewById(R.id.location_dropdown)
        ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        navigation =  findViewById(R.id.bottom_navigation)

        navigation.selectedItemId = R.id.home

        navigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.account -> {
                    val intent = Intent(this, Account::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)

                    finish()
                    true
                }
                R.id.basket -> {
                    val intent = Intent(this, BasketActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)

                    finish()
                    true
                }
                else -> false
            }
        }
        //setContent {
          //  val drinksList = remember { mutableStateOf<List<Drink_Menu>>(emptyList()) }
            //val isLoading = remember { mutableStateOf(true) }

            // Load drinks from Firebase
           // LaunchedEffect(Unit) {
            //    drinksRef.addValueEventListener(object : ValueEventListener {
               //     override fun onDataChange(snapshot: DataSnapshot) {
                    //    val tempList = mutableListOf<Drink_Menu>()

                        // If database is empty, add default drinks
                     //   if (!snapshot.exists()) {
                       //     Drink_Menu.defaultDrinks.forEach { drink ->
                       //        val key = drinksRef.push().key
                                //Null will crash app
                         //       if (key != null) drinksRef.child(key).setValue(drink)
                         //  }
                       // }

                        // Populate tempList with drinks from Firebase
                      //  for (child in snapshot.children) {
                      //     val drink = child.getValue(Drink_Menu::class.java)
                      //      if (drink != null) tempList.add(drink)
                       // }

                     //   drinksList.value = tempList
                      //  isLoading.value = false
                   // }

                  //  override fun onCancelled(error: DatabaseError) {
                     //   isLoading.value = false
                   // }
               // })
                //testing
               // val testDrink = Drink_Menu(
                 //   name = "Test Drink",
                   // Drink_Type = "Cold",
                    //price = 2,
                    //ingredients = listOf("Ingredient1", "Ingredient2")
                //)
               //addItem(testDrink) // adds the drink to Firebase

                // --- Testing removeDrink ---
                // Remove the test drink after 5 seconds (just for testing)
                //kotlinx.coroutines.delay(5000)
               // removeDrink(testDrink.id)
            //}

            // Main page UI
            //Column(
              //  modifier = Modifier
                //    .fillMaxSize()
                  //  .padding(16.dp),
               // horizontalAlignment = Alignment.CenterHorizontally
            //) {
                // Greeting at the top
              //  Text(
                //    text = "Hello User!",
                  //  style = MaterialTheme.typography.titleLarge,
                   // modifier = Modifier.padding(bottom = 16.dp)
                //)

                // Show loading / empty / menu
               // when {
                 //   isLoading.value -> Text("Loading menu...")
                   // drinksList.value.isEmpty() -> Text("No drinks available yet")
                    //else -> {
                      //  Text(
                        //    text = "Menu:",
                          //  style = MaterialTheme.typography.titleMedium,
                           // modifier = Modifier.padding(bottom = 8.dp)
                        //)

                      //  LazyColumn(
                        //    modifier = Modifier.fillMaxWidth(),
                          //  verticalArrangement = Arrangement.spacedBy(8.dp)
                        //) {
                          //  items(drinksList.value) { drink ->
                            //    Text(
                                    //Update for better list UI??
                              //      text = "${drink.name} ${drink.Drink_Type}: ${drink.ingredients.joinToString(", ")}, it costs $${drink.price}",
                                //    modifier = Modifier.padding(vertical = 4.dp)
                                //)
                          //  }
                       // }
                    //}
                //}
            //}
        }
    }
//}

val drinksRef = FirebaseDatabase.getInstance().getReference("drinks")
// Function to add or update a user
    fun addItem(drink: Drink_Menu): String? {
        val drinkId = drink.id.ifEmpty { drinksRef.push().key } ?: return null
        drink.id = drinkId

        drinksRef.child(drinkId).setValue(drink)
        return drinkId
    }

fun removeDrink(userId: String) {
    drinksRef.child(userId).removeValue()
}