package com.example.qr_scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qr_scanner.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var scannerView: ZXingScannerView
    private lateinit var resultTextView: TextView

    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scannerView = findViewById(R.id.scanner_view)
        resultTextView = findViewById(R.id.result_text_view)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            startScanner()
        }
    }

    private fun startScanner() {
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun handleResult(rawResult: Result?) {
        rawResult?.let {
            val resultText = rawResult.text
            resultTextView.text = resultText
            resultTextView.visibility = TextView.VISIBLE

            // Optionally handle actions based on the scanned content
            if (rawResult.barcodeFormat == BarcodeFormat.QR_CODE) {
                // If QR code contains a URL, you can open it in a browser
                if (resultText.startsWith("http://") || resultText.startsWith("https://")) {
                    val openUrlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resultText))
                    startActivity(openUrlIntent)
                } else {
                    // Handle other types of QR code content as needed
                    // For example, display a message or perform specific actions
                    Toast.makeText(this, "Scanned QR code content: $resultText", Toast.LENGTH_LONG).show()
                }
            }

            // Resume scanning
            scannerView.resumeCameraPreview(this)
        }
    }
}
