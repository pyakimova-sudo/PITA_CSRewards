package com.example.pita_rewards2
import android.media.Image
import java.util.UUID

data class Drink_Menu(
    var name: String = "",
    val price: Int = 0,
    val image: Int
) : java.io.Serializable{
    companion object {
        // Default drinks that will be added to Firebase if the database is empty
        val defaultDrinks = listOf(
            Drink_Menu(
                name = "Latte",
                price = 5,
                image = R.drawable.latte
            ),
            Drink_Menu(
                name = "Smoothie",
                price = 3,
                image = R.drawable.smoothie
            ),
            Drink_Menu(
                name = "Matcha",
                price = 5,
                image = R.drawable.matcha
            ),
            Drink_Menu(
                name = "Cold Brew",
                price = 5,
                image = R.drawable.cold_brew
            ),
            Drink_Menu(
                name = "Water",
                price = 1,
                image = R.drawable.water
            ),
            Drink_Menu(
                name = "Lemonade",
                price = 3,
                image = R.drawable.lemonade
            ),
            Drink_Menu(
                name = "Tea",
                price = 3,
                image = R.drawable.tea
            ),
            Drink_Menu(
                name = "Hot Chocolate",
                price = 5,
                image = R.drawable.hot_chocolate
            ),
            Drink_Menu(name = "Milk", price = 1, image = R.drawable.milk),
            Drink_Menu(
                name = "Mocha",
                price = 5,
                image = R.drawable.mocha
            )
        )
    }
}
