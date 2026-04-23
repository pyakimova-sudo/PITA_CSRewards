package com.example.pita_rewards2.checkoutActivities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pita_rewards2.R
import com.example.pita_rewards2.mainActivities.DisabledButtons
import com.example.pita_rewards2.mainActivities.Drink_Menu

class UnavailableAdapter(
    private val drinkList: ArrayList<Drink_Menu>,
    private val context: android.content.Context
) : RecyclerView.Adapter<UnavailableAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.drink_name)
        val image: ImageView = itemView.findViewById(R.id.image)
        val availableSwitch: SwitchCompat = itemView.findViewById(R.id.availableSwitch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.unavailable_view_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = drinkList[position]
        holder.name.text = currentItem.name
        holder.image.setImageResource(currentItem.image)

        holder.availableSwitch.setOnCheckedChangeListener(null)

        holder.availableSwitch.isChecked = DisabledButtons.isDisabled(currentItem.name)

        holder.availableSwitch.setOnCheckedChangeListener { _, isChecked ->
            DisabledButtons.setDisabled(currentItem.name, isChecked)
            val status = if (isChecked) "disabled" else "enabled"
            Toast.makeText(context, "${currentItem.name} has been $status", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = drinkList.size
}