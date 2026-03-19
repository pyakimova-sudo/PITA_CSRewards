package com.example.pita_rewards2
import java.util.UUID

data class Drink_Menu(
    var id: String = "",
    val name: String = "",
    val Drink_Type: String = "",
    val ingredients: List<String> = emptyList()
){
    companion object {
        // Default drinks that will be added to Firebase if the database is empty
        val defaultDrinks = listOf(
            Drink_Menu(
                name = " Brown",
                Drink_Type = "Coffee",
                ingredients = listOf("Coffee", "Milk", "Ice")
            ),
            Drink_Menu(
                name = "Strawberry Smoothie",
                Drink_Type = "Smoothie",
                ingredients = listOf("Strawberry", "Milk", "Banana")
            ),
            Drink_Menu(name = "Milk", Drink_Type = "Base?", ingredients = listOf("Milk", "ice"))
        )
    }
}
