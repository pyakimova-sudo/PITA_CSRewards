package com.example.pita_rewards2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val drinksRef = FirebaseDatabase.getInstance().getReference("drinks")

        setContent {
            val drinksList = remember { mutableStateOf<List<Drink_Menu>>(emptyList()) }
            val isLoading = remember { mutableStateOf(true) }

            // Load drinks from Firebase
            LaunchedEffect(Unit) {
                drinksRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val tempList = mutableListOf<Drink_Menu>()

                        // If database is empty, add default drinks
                        if (!snapshot.exists()) {
                            Drink_Menu.defaultDrinks.forEach { drink ->
                                val key = drinksRef.push().key
                                //Null will crash app
                                if (key != null) drinksRef.child(key).setValue(drink)
                            }
                        }

                        // Populate tempList with drinks from Firebase
                        for (child in snapshot.children) {
                            val drink = child.getValue(Drink_Menu::class.java)
                            if (drink != null) tempList.add(drink)
                        }

                        drinksList.value = tempList
                        isLoading.value = false
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isLoading.value = false
                    }
                })
                //testing
                val testDrink = Drink_Menu(
                    name = "Test Drink",
                    Drink_Type = "Cold",
                    price = 2,
                    ingredients = listOf("Ingredient1", "Ingredient2")
                )
                addItem(testDrink) // adds the drink to Firebase

                // --- Testing removeDrink ---
                // Remove the test drink after 5 seconds (just for testing)
                kotlinx.coroutines.delay(5000)
                removeDrink(testDrink.id)
            }

            // Main page UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Greeting at the top
                Text(
                    text = "Hello User!",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Show loading / empty / menu
                when {
                    isLoading.value -> Text("Loading menu...")
                    drinksList.value.isEmpty() -> Text("No drinks available yet")
                    else -> {
                        Text(
                            text = "Menu:",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(drinksList.value) { drink ->
                                Text(
                                    //Update for better list UI??
                                    text = "${drink.name} ${drink.Drink_Type}: ${drink.ingredients.joinToString(", ")}, it costs $${drink.price}",
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

val drinksRef = FirebaseDatabase.getInstance().getReference("drinks")
// Function to add or update a user
fun addItem(drink: Drink_Menu) {
    fun addItem(drink: Drink_Menu): String? {
        val drinkId = drink.id.ifEmpty { drinksRef.push().key } ?: return null
        drink.id = drinkId

        drinksRef.child(drinkId).setValue(drink)
        return drinkId
    }
}

fun removeDrink(userId: String) {
    drinksRef.child(userId).removeValue()
}