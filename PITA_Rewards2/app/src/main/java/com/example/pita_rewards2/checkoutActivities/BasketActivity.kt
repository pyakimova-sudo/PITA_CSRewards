package com.example.pita_rewards2.checkoutActivities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pita_rewards2.databinding.ActivityBasketBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.CheckBox
import com.example.pita_rewards2.R
import com.example.pita_rewards2.mainActivities.MainActivity
import com.example.pita_rewards2.userActivities.Account
import com.google.firebase.database.FirebaseDatabase
import android.widget.Button
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.example.pita_rewards2.QrScanner

//TODO make phone notification for order completion
class BasketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasketBinding
    lateinit var navigation: BottomNavigationView
    private lateinit var database: FirebaseDatabase
    private lateinit var orderContainer: LinearLayout

    private lateinit var totalText: TextView
    private lateinit var subtotalText: TextView

    private lateinit var locationSpinner: Spinner
    private lateinit var qrScan: Button
    private lateinit var pointCheckbox: CheckBox
    private var userId: String = ""
    private var userPoints: Int = 0

    private val scannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ){result ->
        if (result.contents == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
        else {
            val scannedText = result.contents.trim()
            if (scannedText == "weeklydeal") {
                Toast.makeText(this, "Weekly deal has been applied!", Toast.LENGTH_SHORT).show()
                weeklyDeal.activateWeeklyDeal()
            } else {
                Toast.makeText(this, "Unknown", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val points = intent.getStringExtra("points")
        val userId = intent.getStringExtra("userId")

        binding = ActivityBasketBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        userId = intent.getStringExtra("userId") ?: ""
        var pointsRef = database.getReference("users").child(userId).child("points")
        pointsRef.get().addOnSuccessListener { snapshot ->
            userPoints = snapshot.getValue(Int::class.java)?: 0
        }


        orderContainer = findViewById(R.id.orderContainer)
        totalText = findViewById(R.id.totalTxt)
        subtotalText = findViewById(R.id.totalFeeTxt)
        locationSpinner = findViewById(R.id.location_dropdown)
        qrScan = findViewById(R.id.qr_scan)
        pointCheckbox = findViewById(R.id.points)

        ArrayAdapter.createFromResource(
            this, R.array.locations, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationSpinner.adapter = adapter
        }

        displayOrders()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        binding.qrScan.setOnClickListener {
            startActivity(Intent(this, QrScanner::class.java))
            finish()
        }

        binding.btnCheckout.setOnClickListener {
            val userId = intent.getStringExtra("userId")
            val validOrders = MainActivity.customizations.filter { it.drink.isNotEmpty() }

            if (validOrders.isEmpty()) {
                Toast.makeText(this, "Basket is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId.isNullOrEmpty()) {
                Toast.makeText(this, "User ID is missing!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedLocation = locationSpinner.selectedItem.toString()
            validOrders.forEach { it.location = selectedLocation }

            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("location", selectedLocation)
            startActivity(intent)
        }

        // QR SCAN
        binding.qrScan.setOnClickListener {
            scannerLauncher.launch(
                ScanOptions().setPrompt("Scan QR code")
                    .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            )
        }

        // POINT CHECKBOX
        val pointCheckbox = findViewById<CheckBox>(R.id.points)
        pointCheckbox.setOnCheckedChangeListener { _:Any, isChecked: Boolean ->
            val currentTotal = MainActivity.customizations.sumOf { it.price }
            if (isChecked) {
                Toast.makeText(this, "Points have been applied!", Toast.LENGTH_LONG).show()
                //pointsRef.setValue(0)
                pointsActivated.status = true
                if (pointsActivated.priceOff(userPoints) > currentTotal) {
                    pointsActivated.pointsUsed = (200 * currentTotal).toInt()
                    //pointsRef -= pointsActivated.pointsUsed
                    pointsRef.setValue(userPoints - pointsActivated.pointsUsed)
                } else {
                    pointsActivated.pointsUsed = userPoints
                    userPoints = 0
                }
                calculateTotal()
            } else {
                Toast.makeText(this, "Points have been unapplied", Toast.LENGTH_SHORT).show()
                pointsActivated.status = false
                calculateTotal()
            }
        }

        navigation = findViewById(R.id.bottom_navigation)
        navigation.selectedItemId = R.id.basket

        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("userId",userId)
                    intent.putExtra("points", points)
                    startActivity(intent)

                    finish()
                    true
                }

                R.id.account -> {
                    val intent = Intent(this, Account::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("userId",userId)
                    intent.putExtra("points", points)
                    startActivity(intent)

                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun calculateTotal() {
        val currentTotal = MainActivity.customizations.sumOf { it.price }
        var subtotal = currentTotal.toDouble()
        if (pointsActivated.status) {
            subtotal -= pointsActivated.priceOff(pointsActivated.pointsUsed)
        }
        if (weeklyDeal.applied == true) {
            subtotal -= 2
        }
        totalText.text = "$$currentTotal"
        subtotalText.text = "$$subtotal"

    }

    private fun displayOrders() {
        val drinkCustomizations = MainActivity.customizations
        val inflater = LayoutInflater.from(this)

        orderContainer.removeAllViews()

        for (order in drinkCustomizations) {
            val itemView = inflater.inflate(R.layout.viewholder_basket, orderContainer, false)

            if (MainActivity.isOrderSubmitted) {
                binding.paymentLayout.visibility = android.view.View.INVISIBLE
                itemView.findViewById<TextView>(R.id.statusText).visibility = android.view.View.VISIBLE
            } else {
                binding.paymentLayout.visibility = android.view.View.VISIBLE
                itemView.findViewById<TextView>(R.id.statusText).visibility = android.view.View.INVISIBLE
            }

        for (order in drinkCustomizations) {
            val itemView = inflater.inflate(R.layout.viewholder_basket, orderContainer, false)
            val statusText = itemView.findViewById<TextView>(R.id.statusText)

            val picCart = itemView.findViewById<ImageView>(R.id.picCart)
            picCart.setImageResource(order.imageResourceId)

            val drinkNameText = itemView.findViewById<TextView>(R.id.drinkNameText)
            drinkNameText.text = order.drink

            val orderItems = itemView.findViewById<TextView>(R.id.orderItems)
            val detailsList = listOfNotNull(
                order.size.takeIf { it.isNotEmpty() }?.let { "Size: $it" },
                order.temp.takeIf { it.isNotEmpty() }?.let { "Temp: $it" }, // Added Temp
                order.milk.takeIf { it.isNotEmpty() && it != "None" }?.let { "Milk: $it" },
                order.sweetness.takeIf { it.isNotEmpty() }?.let { "Sweetness: $it" },
                order.extraDetails.takeIf { it.isNotEmpty() }
            )
            orderItems.text = detailsList.joinToString("\n")

            //Quantity indicator
            val quantityText = itemView.findViewById<TextView>(R.id.quantityText)
            quantityText.text = "x${order.quantity}"

            //Price by amount
            val totalFee = itemView.findViewById<TextView>(R.id.totalFee)
            totalFee.text = "$${order.price * order.quantity}"

            // Remove items when order is submitted
            val removeBtn = itemView.findViewById<ImageView>(R.id.removeItemButton)

            //decrease quantity of order
            if (MainActivity.isOrderSubmitted) {
                removeBtn.visibility = android.view.View.GONE
                statusText.visibility = android.view.View.VISIBLE
            } else {
                removeBtn.visibility = android.view.View.VISIBLE
                statusText.visibility = android.view.View.INVISIBLE
                removeBtn.setOnClickListener {
                    if (order.quantity > 1) {
                        order.quantity -= 1
                    } else {
                        MainActivity.customizations.remove(order)
                    }
                    displayOrders()
            }
        }
            orderContainer.addView(itemView)
        }
        calculateTotal()
    }

    override fun onResume() {
        super.onResume()
        displayOrders()
    }
}


data class user_orders(
    //TODO how store what data
    val orders: MutableList<String>
)

object pointsActivated {
    var status: Boolean = false
    var pointsUsed = 0
    fun priceOff(points: Int): Double {
        return points.toDouble() / 200.0
    }
}

object weeklyDeal {
    var applied: Boolean = false

    fun activateWeeklyDeal() {
        applied = true
    }
}