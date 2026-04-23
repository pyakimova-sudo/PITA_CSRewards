package com.example.pita_rewards2.mainActivities
import com.example.pita_rewards2.R
import java.io.Serializable

data class Drink_Menu(
    var id: String = "",
    var name: String = "",
    val Drink_Type: String = "",
    val price: Int = 0,
    val image: Int,
    var isAvailable: Boolean = true,
    var quantity: Int = 1,
    val ingredients: List<String> = emptyList()
) : Serializable{
    companion object {
        // Default drinks that will be added to Firebase if the database is empty
        val defaultDrinks = listOf(
            Drink_Menu(
                name = "Latte",
                Drink_Type = "Coffee", //Will be used to sort might need another button
                price = 2,
                ingredients = listOf("Coffee", "Milk", "Ice"),
                image = R.drawable.latte

            ),
            Drink_Menu(
                name = "Smoothie",
                Drink_Type = "Smoothie",
                price = 1,
                ingredients = listOf(""),
                image = R.drawable.smoothie
            ),
            Drink_Menu(
                name = "Matcha",
                Drink_Type = "Matcha",
                price = 1,
                ingredients = listOf("Coffee", "Milk", "Ice"),
                image = R.drawable.matcha
            ),
            Drink_Menu(
                name = "Cold Brew",
                Drink_Type = "Coffee",
                price = 1,
                ingredients = listOf("Coffee", "Milk", "Ice"),
                image = R.drawable.cold_brew
            ),
            Drink_Menu(
                name = "Water",
                Drink_Type = "Regular",
                price = 0,
                ingredients = listOf("Water"),
                image = R.drawable.water
            ),
            Drink_Menu(
                name = "Lemonade",
                Drink_Type = "Lemonade",
                price = 2,
                ingredients = listOf("Ice"),
                image = R.drawable.lemonade
            ),
            Drink_Menu(
                name = "Tea",
                Drink_Type = "Tea",
                price = 3,
                ingredients = listOf("Milk", "Tea"),
                image = R.drawable.tea
            ),
            Drink_Menu(
                name = "Hot Chocolate",
                Drink_Type = "Chocolate",
                price = 86,
                ingredients = listOf("Chocolate", "Milk"),
                image = R.drawable.hot_chocolate
            ),
            Drink_Menu(
                name = "Milk",
                Drink_Type = "Base?",
                price = 1,
                ingredients = listOf("Milk", "ice"),
                image = R.drawable.milk
            ),
            Drink_Menu(
                name = "Mocha",
                price = 5,
                ingredients = listOf("Milk", "ice"),
                image = R.drawable.mocha
            ),
            Drink_Menu(
                name = "Americano",
                price = 3,
                image = R.drawable.americano
            ),
        )
    }
}
