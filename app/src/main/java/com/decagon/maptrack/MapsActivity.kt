package com.decagon.maptrack

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.decagon.maptrack.model.MyLocationLog
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val MY_TITLE = "Dika"
    private val PARTNER_TITLE = "Kome"

    lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Partner Location listener updates
        dbReference = Firebase.database.reference
        dbReference.addValueEventListener(eventListener)


    }


    /**
     * Read partner's location from database by listening to change of her location
     */
    private val eventListener = object : ValueEventListener {

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(applicationContext, "Could not read from database", Toast.LENGTH_LONG)
                .show()
        }


        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                map.clear()

                //Get partner's location from firebase
                val partnerLocationLog =
                    dataSnapshot.child("komesLocation").getValue(MyLocationLog::class.java)

                var partnerLatitude = partnerLocationLog?.Latitude
                var partnerLongitude = partnerLocationLog?.Longitude


                if (partnerLatitude != null && partnerLongitude != null) {
                    val partnerLocation = LatLng(partnerLatitude, partnerLongitude)

                    val markerOptions =
                        MarkerOptions().position(partnerLocation).title(PARTNER_TITLE)
                    val zoomLevel = 15f

                    ///Custom Marker
                    val bitmap: Bitmap? = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(
                            resources,
                            R.drawable.kome
                        ), 150, 150, true
                    )
                    map.addMarker(markerOptions).setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    map.setMinZoomPreference(zoomLevel)
                    map.animateCamera(CameraUpdateFactory.newLatLng(partnerLocation))

                }

                //Fetch my location my firebase
                val myLocationLog =
                    dataSnapshot.child("dikasLocation").getValue(MyLocationLog::class.java)


                if (myLocationLog != null) {
                    var myLatitude = myLocationLog.Latitude
                    var myLongitude = myLocationLog.Longitude
                    val zoomLevel = 15f


                    val latLng = LatLng(myLatitude!!, myLongitude!!)
                    val markerOptions = MarkerOptions().position(latLng).title(MY_TITLE)
                    ///Custom Marker
                    val bitmap: Bitmap? = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(
                            resources,
                            R.drawable.dika
                        ), 150, 150, true
                    )
                    map.addMarker(markerOptions).setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    map.setMinZoomPreference(zoomLevel)

                    //           map.animateCamera(CameraUpdateFactory.newLatLng(latLng))

                }
            }
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
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.setAllGesturesEnabled(true)


        getLocationAccess()
    }

    //Get location access if permission granted
    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            getLocationUpdates()
            startLocationUpdates()
        } else
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
    }

    //Permission request handler
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
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
                    return
                }
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "User has not granted location access permission",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        return true
    }

    //Different maps selection
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        //Change the map type based on the user's preference
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }

        else -> super.onOptionsItemSelected(item)
    }


    /**
     * Set location updates
     */
    private fun getLocationUpdates() {
        locationRequest = LocationRequest()


        val MIN_TIME = 10000L
        val TIME_INTERVAL = 20000L


        //Set intervals
        locationRequest.interval = TIME_INTERVAL
        locationRequest.fastestInterval = MIN_TIME
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation

                    dbReference = Firebase.database.reference

                    val locationLogging = MyLocationLog(location.latitude, location.longitude)
                    dbReference.child("dikasLocation").setValue(locationLogging)
                        .addOnSuccessListener {
//                            Toast.makeText(applicationContext, "Locations written into the database", Toast.LENGTH_LONG).show()
                        }


                }
            }
        }
    }

    /**
     * Register location callback with location client
     */
    private fun startLocationUpdates() {
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

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }


}