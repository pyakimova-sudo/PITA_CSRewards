package com.example.pita_rewards2

data class UserData(
    val id: String? = null,
    val username: String? = null,
    val password: String? = null,
    val phone: String? = null,
    val studentID: String? = null,
    //?? will it save and update over update
    var points: Int = 0 //mutable int
)
