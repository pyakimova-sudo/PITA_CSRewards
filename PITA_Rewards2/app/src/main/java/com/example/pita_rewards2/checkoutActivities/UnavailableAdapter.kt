package com.example.pita_rewards2.mainActivities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pita_rewards2.R
import com.google.firebase.database.FirebaseDatabase

class UnavailableAdapter(private val drinkMenu: ArrayList<Drink_Menu>, private val listener: RecyclerViewEvent): RecyclerView.Adapter<UnavailableAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): ViewHolderClass {
        val itemView = LayoutInflater.from(p0.context).inflate(R.layout.unavailable_view_row, p0, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(
        p0: ViewHolderClass,
        p1: Int
    ) {

        if (p1 == drinkMenu.size - 1) {
            val params = p0.itemView.getLayoutParams() as RecyclerView.LayoutParams
            params.bottomMargin = 100 // last item bottom margin
            p0.itemView.setLayoutParams(params)
        } else {
            val params = p0.itemView.getLayoutParams() as RecyclerView.LayoutParams
            params.bottomMargin = 10 // other items bottom margin
            p0.itemView.setLayoutParams(params)
        }


        val currentItem = drinkMenu[p1]
        p0.rvImage.setImageResource(currentItem.image)
        p0.rvDrink.text = currentItem.name
        p0.switch.setOnCheckedChangeListener(null)
        p0.switch.isChecked = currentItem.isAvailable
        p0.switch.setOnCheckedChangeListener { _, isChecked ->
            currentItem.isAvailable = isChecked
            val database = FirebaseDatabase.getInstance().getReference("drinks")
            database.child(currentItem.name).child("isAvailable").setValue(isChecked)
        }
    }

    override fun getItemCount(): Int {
        return drinkMenu.size
    }

    inner class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val rvImage: ImageView = itemView.findViewById(R.id.image)
        val rvDrink: TextView = itemView.findViewById(R.id.drink_name)
        val switch: SwitchCompat = itemView.findViewById(R.id.availableSwitch)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)            }
        }
    }

    interface RecyclerViewEvent{
        fun onItemClick(position: Int)
    }
}