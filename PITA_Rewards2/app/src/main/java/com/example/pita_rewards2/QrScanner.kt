package com.example.pita_rewards2
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.example.pita_rewards2.checkoutActivities.weeklyDeal
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class QrScanner : AppCompatActivity() {
    private lateinit var qrScan: Button
    private lateinit var scannedValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_qr_scanner)
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
        //    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        //    insets
        //}

        qrScan = findViewById(R.id.qr_scan)
        scannedValue = findViewById(R.id.scanned_value)

        registerUIListener()
    }

    private fun registerUIListener() {
        qrScan.setOnClickListener {
            scannerLauncher.launch(ScanOptions().setPrompt("Scan QR code").setDesiredBarcodeFormats(ScanOptions.QR_CODE))
        }
    }

    private val scannerLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ){result ->
        if (result.contents == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
        else {
            val scannedText = result.contents.trim()
            scannedValue.text = "Scanned Value: $scannedText"
            if (scannedText == "weeklydeal") {
                Toast.makeText(this, "Weekly deal has been applied!", Toast.LENGTH_SHORT).show()
                weeklyDeal.activateWeeklyDeal()
            } else {
                Toast.makeText(this, "Unknown", Toast.LENGTH_SHORT).show()
            }
            //scannedValue.text = buildString {
            //    append("Scanned Value: ")
             //   append(result.contents)
            //}
        }
    }
}