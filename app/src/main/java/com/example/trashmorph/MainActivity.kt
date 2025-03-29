package com.example.trashmorph

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Check if the location permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Permission granted, check if location services are enabled
            checkLocationEnabled()
        }

        // Card click listeners
        val cardView = findViewById<CardView>(R.id.cardView1)
        cardView.setOnClickListener {
            val intent = Intent(this, RecycledArtActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Explore the products", Toast.LENGTH_SHORT).show()
        }

        val cardView1 = findViewById<CardView>(R.id.cardView2)
        cardView1.setOnClickListener {
            val intent = Intent(this, RecycledArtActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Explore the products", Toast.LENGTH_SHORT).show()

        }

        // Image click listeners
        val imageView = findViewById<ImageView>(R.id.plastic)
        imageView.setOnClickListener {
            val intent = Intent(this, ImageUploadActivity::class.java)
            startActivity(intent)
        }

        val imageView2 = findViewById<ImageView>(R.id.Ewaste)
        imageView2.setOnClickListener {
            val intent = Intent(this, ImageUploadActivity::class.java)
            startActivity(intent)
        }

        val imageView3 = findViewById<ImageView>(R.id.metal)
        imageView3.setOnClickListener {
            val intent = Intent(this, ImageUploadActivity::class.java)
            startActivity(intent)
        }

        val imageView4 = findViewById<ImageView>(R.id.paper)
        imageView4.setOnClickListener {
            val intent = Intent(this, ImageUploadActivity::class.java)
            startActivity(intent)
        }

        // Ensure 'main' view is available in your layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Check if location services are enabled
    private fun checkLocationEnabled() {
        if (!isLocationEnabled()) {
            // If location services are off, prompt user to enable it
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    // Check if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // Handle the result of the location permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, check if location is enabled
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
                checkLocationEnabled()
            } else {
                // Permission denied
                // Check if the user has denied the permission permanently
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // The user has denied permission but can still request it again
                    Toast.makeText(this, "Location permission denied. Please enable it.", Toast.LENGTH_LONG).show()
                } else {
                    // The user has denied permission and selected 'Don't ask again' or it was denied permanently
                    Toast.makeText(this, "Location permission denied permanently. Please enable it in settings.", Toast.LENGTH_LONG).show()
                    openSettings()
                }
            }
        }
    }

    // Open the settings screen for the user to enable location permission
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = android.net.Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}
