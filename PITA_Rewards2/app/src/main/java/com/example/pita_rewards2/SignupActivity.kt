package com.example.pita_rewards2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pita_rewards2.databinding.ActivitySignupBinding
import com.google.firebase.database.*
import kotlin.jvm.java
import kotlin.text.isNotEmpty

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        binding.signUpButton.setOnClickListener {
            //calls to UserData
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword = binding.signupPassword.text.toString()
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val phone = binding.phone.text.toString()
            val studentID = binding.studentID.text.toString().trim()//Removes extra spacing

            //All fields must be filled(Can make any optional if desired)
            if (signupUsername.isNotEmpty() && signupPassword.isNotEmpty() &&
                firstName.isNotEmpty() && lastName.isNotEmpty() &&
                phone.isNotEmpty() && studentID.isNotEmpty()) {
                signupUser(signupUsername, signupPassword,firstName, lastName, phone, studentID)
            } else {
                Toast.makeText(this, "All fields are mandatory!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun signupUser(username: String, password: String, firstName:String, lastname:String,phone:String,studentID:String) {
        //??
        databaseReference.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                //??
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        val id = databaseReference.push().key
                        if (id != null) {
                            val userData = UserData(id, username, password, phone, studentID)
                            databaseReference.child(id).setValue(userData)
                            Toast.makeText(this@SignupActivity, "Signup Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@SignupActivity, "Error generating user ID", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@SignupActivity, "User Already Exists", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SignupActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}