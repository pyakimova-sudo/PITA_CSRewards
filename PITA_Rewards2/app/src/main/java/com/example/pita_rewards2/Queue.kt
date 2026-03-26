package com.example.pita_rewards2
//package com.example.data

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

data class Queue(
    var id: String = "",
    val customerName: String = "",
    val items: List<String> = emptyList()
)//{
    //companion object {

    //}
