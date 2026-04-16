package com.example.pita_rewards2.mainActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pita_rewards2.checkoutActivities.EmployeeActivity
import com.example.pita_rewards2.userActivities.UserData
import com.example.pita_rewards2.databinding.ActivityLoginBinding
import com.google.firebase.database.*
import com.example.pita_rewards2.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        binding.loginButton.setOnClickListener {
            val loginUsername = binding.loginUsername.text.toString()
            val loginPassword = binding.loginPassword.text.toString()

            if (loginUsername.isNotEmpty() && loginPassword.isNotEmpty()) {
                loginUser(loginUsername, loginPassword)
            } else {
                Toast.makeText(this, "All fields are mandatory!!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupRedirect.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        //Employee login
        binding.employeeLogin.setOnClickListener {
            //Directly passing userId to EmployeeActivity
            val userId = intent.getStringExtra("userId")
            if (userId != null) {
                val intent = Intent(this, EmployeeActivity::class.java)
                intent.putExtra("userId", userId)  // Pass the userId to EmployeeActivity
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "User ID is missing", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(username: String, password: String) {
        databaseReference.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            val userData = userSnapshot.getValue(UserData::class.java)
                            if (userData != null && userData.password == password) {
                                Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                                val id = userSnapshot.key
                                // Pass userId to MainActivity
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.putExtra("userId", id)
                                startActivity(intent)
                                finish()
                                return
                            }
                        }
                    }
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}