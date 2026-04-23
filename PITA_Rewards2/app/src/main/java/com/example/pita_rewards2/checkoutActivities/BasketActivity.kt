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
import com.example.pita_rewards2.R
import com.example.pita_rewards2.mainActivities.MainActivity
import com.example.pita_rewards2.userActivities.Account
import com.google.firebase.database.FirebaseDatabase
import android.widget.Button
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

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

        private val scannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        } else {
            val scannedValue = result.contents
            Toast.makeText(this, "Scanned: $scannedValue", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("userId")

        binding = ActivityBasketBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)


        orderContainer = findViewById(R.id.orderContainer)
        totalText = findViewById(R.id.totalTxt)
        subtotalText = findViewById(R.id.totalFeeTxt)
        locationSpinner = findViewById(R.id.location_dropdown)
        qrScan = findViewById(R.id.qr_scan)

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

        binding.btnCheckout.setOnClickListener {
            val userId = intent.getStringExtra("userId")
            if (userId.isNullOrEmpty()) {
                Toast.makeText(this, "User ID is missing!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selectedLocation = locationSpinner.selectedItem.toString()

            MainActivity.customizations.forEach { it.location = selectedLocation }

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

        navigation = findViewById(R.id.bottom_navigation)
        navigation.selectedItemId = R.id.basket

        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("userId", userId)
                    startActivity(intent)

                    finish()
                    true
                }

                R.id.account -> {
                    val intent = Intent(this, Account::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("userId", userId)
                    startActivity(intent)

                    finish()
                    true
                }

                else -> false
            }
        }
    }

    private fun calculateTotal() {
        val total = MainActivity.customizations.sumOf { it.price }
        var subtotal = total
        if (weeklyDeal.applied == true) {
            subtotal -= 2
        }
        totalText.text = "$$total"
        subtotalText.text = "$$subtotal"

    }

    private fun displayOrders() {
        val drinkCustomizations = MainActivity.customizations
        val inflater = LayoutInflater.from(this)

        orderContainer.removeAllViews()

        if (MainActivity.isOrderSubmitted) {
            binding.paymentLayout.visibility = android.view.View.INVISIBLE
        } else {
            binding.paymentLayout.visibility = android.view.View.VISIBLE
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

            val totalFee = itemView.findViewById<TextView>(R.id.totalFee)
            totalFee.text = "$${order.price}"

            // Remove items when order is submitted
            val removeBtn = itemView.findViewById<ImageView>(R.id.removeItemButton)

            if (MainActivity.isOrderSubmitted) {
                removeBtn.visibility = android.view.View.GONE
                statusText.visibility = android.view.View.VISIBLE
            } else {
                removeBtn.visibility = android.view.View.VISIBLE
                statusText.visibility = android.view.View.INVISIBLE
                removeBtn.setOnClickListener {
                    MainActivity.customizations.remove(order)
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

    object weeklyDeal {
        var applied: Boolean = false

        fun activateWeeklyDeal() {
            applied = true
        }
    }
}