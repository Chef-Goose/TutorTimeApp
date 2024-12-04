package com.example.tutorapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class StudentInstantSession : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContentView(R.layout.activity_findtutor)

        // Set padding to accommodate system bars (e.g., notch, status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back button click listener
        val btnBack = findViewById<ImageButton>(R.id.back_button)
        btnBack.setOnClickListener {
            val intent = Intent(this@StudentInstantSession, DashboardNavBar::class.java)
            startActivity(intent)
        }

        // WebView for displaying the map
        val mapWebView: WebView = findViewById(R.id.map_webview)
        val webSettings = mapWebView.settings
        webSettings.javaScriptEnabled = true

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Open map button click listener
        val openMapButton = findViewById<Button>(R.id.location_button)
        openMapButton.setOnClickListener {
            // Check for location permissions
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Request location permission if not granted
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    1)
                return@setOnClickListener
            }

            // If permissions are granted, proceed with getting the location
            requestCurrentLocationAndLoadMap(mapWebView, openMapButton)
        }

        // Set up location callback to listen for real-time location updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for (location in it.locations) {
                        currentLocation = location
                        // Once the location is received, call the method to load the map
                        val mapWebView: WebView = findViewById(R.id.map_webview)
                        val openMapButton: Button = findViewById(R.id.location_button)
                        requestCurrentLocationAndLoadMap(mapWebView, openMapButton)
                    }
                }
            }
        }
    }

    // Method to request current location and load the map
    private fun requestCurrentLocationAndLoadMap(mapWebView: WebView, openMapButton: Button) {
        // Check if permissions are still granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If permissions are not granted, request again
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
            return
        }

        // Start location updates to get the current location
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // Update location every 10 seconds
            fastestInterval = 5000 // Fastest update interval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        // Check if we have a valid currentLocation and load the map
        if (currentLocation != null) {
            val latitude = 53.5461
            val longitude = -113.4937
           // val latitude = currentLocation?.latitude
            //val longitude = currentLocation?.longitude
            val mapUrl = "https://www.google.com/maps/@$latitude,$longitude,12z"

            // Load the map in WebView
            mapWebView.visibility = View.VISIBLE
            mapWebView.loadUrl(mapUrl)

            // Optionally, hide the location button after it's clicked
            openMapButton.visibility = View.GONE
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, now get the location and load the map
            val mapWebView: WebView = findViewById(R.id.map_webview)
            val openMapButton: Button = findViewById(R.id.location_button)
            requestCurrentLocationAndLoadMap(mapWebView, openMapButton)
        } else {
            // Permission denied, handle accordingly
            // Optionally, show a message explaining the need for location access
        }
    }

    // Remember to stop location updates when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
