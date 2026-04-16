package com.example.pita_rewards2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.NonDisposableHandle.parent

class AdapterClass(private val drinkMenu: ArrayList<Drink_Menu>): RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {
    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): ViewHolderClass {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.recycler_view_row, p0, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(
        p0: ViewHolderClass,
        p1: Int
    ) {
        val currentItem = drinkMenu[p1]
        p0.rvImage.setImageResource(currentItem.image)
        p0.rvDrink.text = currentItem.name
        p0.rvPrice.text = currentItem.price.toString()
    }

    override fun getItemCount(): Int {
        return drinkMenu.size
    }

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val rvImage: ImageView = itemView.findViewById(R.id.image)
        val rvDrink: TextView = itemView.findViewById(R.id.drink_name)
        val rvPrice: TextView = itemView.findViewById(R.id.price_text)


    }
}