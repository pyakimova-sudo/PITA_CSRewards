package com.example.pita_rewards2.userActivities

data class UserData(
    val id: String? = null,
    val username: String? = null,
    val password: String? = null,
    val phone: String? = null,
    val studentID: String? = null,
    var points: Int = 0,
    val firstName: String? = null,
    val lastName: String? = null
)
