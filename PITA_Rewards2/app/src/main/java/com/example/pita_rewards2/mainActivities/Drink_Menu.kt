package com.example.pita_rewards2.mainActivities
import java.io.Serializable

data class Drink_Menu(
    var id: String = "",
    val name: String = "",
    val Drink_Type: String = "",
    val price: Int = 0,
    val ingredients: List<String> = emptyList()
) : Serializable{
    companion object {
        // Default drinks that will be added to Firebase if the database is empty
        val defaultDrinks = listOf(
            Drink_Menu(
                name = "Latte",
                Drink_Type = "Coffee", //Will be used to sort might need another button
                price = 2,
                ingredients = listOf("Coffee", "Milk", "Ice")
            ),
            Drink_Menu(
                name = "Smoothie",
                Drink_Type = "Smoothie",
                price = 1,
                ingredients = listOf("")
            ),
            Drink_Menu(
                name = "Matcha",
                Drink_Type = "Matcha",
                price = 1,
                ingredients = listOf("Coffee", "Milk", "Ice")
            ),
            Drink_Menu(
                name = "Cold Brew",
                Drink_Type = "Coffee",
                price = 1,
                ingredients = listOf("Coffee", "Milk", "Ice")
            ),
            Drink_Menu(
                name = "Water",
                Drink_Type = "Regular",
                price = 0,
                ingredients = listOf("Water")
            ),
            Drink_Menu(
                name = "Lemonade",
                Drink_Type = "Lemonade",
                price = 2,
                ingredients = listOf("Ice")
            ),
            Drink_Menu(
                name = "Tea",
                Drink_Type = "Tea",
                price = 3,
                ingredients = listOf("Milk", "Tea")
            ),
            Drink_Menu(
                name = "Hot Chocolate",
                Drink_Type = "Chocolate",
                price = 86,
                ingredients = listOf("Chocolate", "Milk")
            ),
            Drink_Menu(name = "Milk", Drink_Type = "Base?", price = 1, ingredients = listOf("Milk", "ice"))
        )
    }
}
