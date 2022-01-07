package com.example.cameraapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cameraapp.data.Locationlogin
import com.example.cameraapp.R
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val LOCATION_PERMISION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationrequest: LocationRequest
    private lateinit var locationcallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            getlocationupdate()
            startlocationupdate()
        } else
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISION
            )


    }

    private fun getlocationupdate() {
        locationrequest = LocationRequest()
        locationrequest.interval = 30000
        locationrequest.fastestInterval = 20000
        locationrequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationcallback = object : LocationCallback() {
            override fun onLocationResult(locationresult: LocationResult) {

                    if (locationresult.locations.isNotEmpty()) {
                        val location = locationresult.lastLocation
                        var databaseRef= FirebaseDatabase.getInstance().getReference("/userlocation")
                        val uid= FirebaseAuth.getInstance().uid?:""

                        // var databaseRef: DatabaseReference = Firebase.database.reference
                        val locationlogin = Locationlogin(location.latitude, location.longitude)
                        databaseRef.child(uid).setValue(locationlogin)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    applicationContext,
                                    "location is written to the data base!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    applicationContext,
                                    "Location could not be written!!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        if (location != null) {
                            val latLng = LatLng(location.latitude, location.longitude)
                            val markerOptions = MarkerOptions().position(latLng)
                            mMap.addMarker(markerOptions)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        }
                    }


            }
        }
    }



        private fun startlocationupdate() {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationClient.requestLocationUpdates(locationrequest, locationcallback, Looper.getMainLooper())

        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == LOCATION_PERMISION) {
                if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                        getLocation()
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        mMap.isMyLocationEnabled = true
                        return
                    }


                    }

                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
                    finish()
                }
            }


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */


    override fun onMapReady(p0: GoogleMap) {
            mMap= p0
            getLocation()
    }
}
