package com.gnovack.novack_hw8

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.gnovack.novack_hw8.databinding.ActivityMainBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.android.PolyUtil
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var  mapFragment: SupportMapFragment
    lateinit var binding: ActivityMainBinding
    var startLoc:Place? = null
    var destLoc:Place? = null
    var curBounds:LatLngBounds? = null
    var curRange:FloatArray = FloatArray(3)

    val taskPool = Executors.newFixedThreadPool(2)
    val client = OkHttpClient()

    val handler = Handler(Looper.myLooper()!!) { msg ->
        if (msg.what == 1){
            val directionJson = JSONObject(msg.obj as String)
            val routes = directionJson.getJSONArray("routes")
            val firstRoute = routes.getJSONObject(0)
            val boundsJson = firstRoute.getJSONObject("bounds")
            val encodedPolyline = firstRoute.getJSONObject("overview_polyline").getString("points")
            val polylineList = PolyUtil.decode(encodedPolyline)
            val northeast = LatLng(boundsJson.getJSONObject("northeast").getDouble("lat"), boundsJson.getJSONObject("northeast").getDouble("lng"))
            val southwest = LatLng(boundsJson.getJSONObject("southwest").getDouble("lat"), boundsJson.getJSONObject("southwest").getDouble("lng"))
            val bounds = LatLngBounds(southwest, northeast)
            val startCoord = startLoc!!.latLng
            val destCoord = destLoc!!.latLng
            mapFragment.getMapAsync { googleMap ->
                googleMap.addPolyline(
                    PolylineOptions()
                    .clickable(true)
                    .addAll(polylineList))
                googleMap.addMarker(MarkerOptions()
                    .title(startLoc!!.name)
                    .position(startCoord!!))
                googleMap.addMarker(MarkerOptions()
                    .title(destLoc!!.name)
                    .position(destCoord!!))
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        } else if (msg.what == 2){
            val placesJson = JSONObject(msg.obj as String)
            val allPlaces = placesJson.getJSONArray("results")
            for (i in 0 until allPlaces.length()){
                val curPlace = allPlaces.getJSONObject(i)
                val location = curPlace.getJSONObject("geometry").getJSONObject("location")
                val lat = location.getDouble("lat")
                val lng = location.getDouble("lng")
                val latLng = LatLng(lat, lng)
                val name = curPlace.getString("name")
                mapFragment.getMapAsync { googleMap ->
                    googleMap.addMarker(MarkerOptions()
                        .title(name)
                        .position(latLng)) }
            }

        }
        true
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, mapFragment).commit()

        mapFragment.getMapAsync(this)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.api_key))
        }

        // Create a new Places client instance.
        Places.createClient(this)

        // Initialize the AutocompleteSupportFragment.
        val startAutocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.start_autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        startAutocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        // Set up a PlaceSelectionListener to handle the response.
        startAutocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                startLoc = place
                Log.i("START", "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("START", "An error occurred: $status")
            }
        })
        val destAutocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.dest_autocomplete_fragment)
                    as AutocompleteSupportFragment

        destAutocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        destAutocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                destLoc = place
                Log.i("DEST", "Place: ${place.name}, ${place.id}")
            }

            override fun onError(status: Status) {
                Log.i("DEST", "An error occurred: $status")
            }
        })
        binding.clear.setOnClickListener {
            startAutocompleteFragment.setText("")
            destAutocompleteFragment.setText("")

            mapFragment.getMapAsync { gMap ->
                gMap.clear()
            }
        }

        binding.navigate.setOnClickListener {
            if(destLoc != null && startLoc != null) {
                taskPool.execute {
                    val message = Message()
                    val url =
                        "https://maps.googleapis.com/maps/api/directions/json".toHttpUrlOrNull()!!
                            .newBuilder()
                            .addQueryParameter("destination", "place_id:${destLoc!!.id}")
                            .addQueryParameter("origin", "place_id:${startLoc!!.id}")
                            .addQueryParameter("key", getString(R.string.api_key))
                            .build()
                    val request = Request.Builder()
                        .url(url)
                        .build()
                    try {
                        client.newCall(request).execute().use { response ->
                            message.obj = response.body!!.string()
                            message.what = 1
                        }
                    } catch (e: IOException) {
                        message.what = 0
                        e.printStackTrace()
                    }
                    handler.sendMessage(message)
                }
            }
        }

        binding.findGas.setOnClickListener {
            mapFragment.getMapAsync { googleMap ->
                googleMap.clear()
            }
            taskPool.execute {
                val message = Message()
                if(curBounds != null) {
                    val curCenter = curBounds!!.center
                    val url =
                        "https://maps.googleapis.com/maps/api/place/textsearch/json".toHttpUrlOrNull()!!
                            .newBuilder()
                            .addQueryParameter("query", "gas station")
                            .addQueryParameter("location", "${curCenter.latitude},${curCenter.longitude}")
                            .addQueryParameter("range", "${curRange[0] / 2}")
                            .addQueryParameter("key", getString(R.string.api_key))
                            .build()
                    val request = Request.Builder()
                        .url(url)
                        .build()
                    try {
                        client.newCall(request).execute().use { response ->
                            message.obj = response.body!!.string()
                            message.what = 2
                        }
                    } catch (e: IOException) {
                        message.what = 0
                        e.printStackTrace()
                    }
                } else {
                    val url =
                        "https://maps.googleapis.com/maps/api/place/textsearch/json".toHttpUrlOrNull()!!
                            .newBuilder()
                            .addQueryParameter("query", "gas station")
                            .addQueryParameter("key", getString(R.string.api_key))
                            .build()
                    val request = Request.Builder()
                        .url(url)
                        .build()
                    try {
                        client.newCall(request).execute().use { response ->
                            message.obj = response.body!!.string()
                            message.what = 2
                        }
                    } catch (e: IOException) {
                        message.what = 0
                        e.printStackTrace()
                    }
                }
                handler.sendMessage(message)
            }
        }

        binding.findRest.setOnClickListener {
            mapFragment.getMapAsync { googleMap ->
                googleMap.clear()
            }
            taskPool.execute {
                val message = Message()
                if(curBounds != null) {
                    val curCenter = curBounds!!.center
                    val url =
                        "https://maps.googleapis.com/maps/api/place/textsearch/json".toHttpUrlOrNull()!!
                            .newBuilder()
                            .addQueryParameter("query", "restaurant")
                            .addQueryParameter("location", "${curCenter.latitude},${curCenter.longitude}")
                            .addQueryParameter("range", "${curRange[0] / 2}")
                            .addQueryParameter("key", getString(R.string.api_key))
                            .build()
                    val request = Request.Builder()
                        .url(url)
                        .build()
                    try {
                        client.newCall(request).execute().use { response ->
                            message.obj = response.body!!.string()
                            message.what = 2
                        }
                    } catch (e: IOException) {
                        message.what = 0
                        e.printStackTrace()
                    }
                } else {
                    val url =
                        "https://maps.googleapis.com/maps/api/place/textsearch/json".toHttpUrlOrNull()!!
                            .newBuilder()
                            .addQueryParameter("query", "restaurant")
                            .addQueryParameter("key", getString(R.string.api_key))
                            .build()
                    val request = Request.Builder()
                        .url(url)
                        .build()
                    try {
                        client.newCall(request).execute().use { response ->
                            message.obj = response.body!!.string()
                            message.what = 2
                        }
                    } catch (e: IOException) {
                        message.what = 0
                        e.printStackTrace()
                    }
                }
                handler.sendMessage(message)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        with(googleMap) {
            setOnCameraIdleListener {
                curBounds = googleMap.projection.visibleRegion.latLngBounds
                Location.distanceBetween(curBounds!!.northeast.latitude, curBounds!!.northeast.longitude, curBounds!!.southwest.latitude, curBounds!!.southwest.longitude, curRange)
            }
            uiSettings.isZoomControlsEnabled = true
        }
        Log.d("MAP", "onMapReady: READY")
    }


}