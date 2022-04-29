package com.gnovack.group30_inclass11

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Executors

//Inclass 11
//MainActivity
//Gabriel Novack

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    val SUCCESS = 1
    val FAILURE = 0

    val taskPool = Executors.newFixedThreadPool(2)
    lateinit var path:List<Pair<Double, Double>>
    lateinit var  mapFragment:SupportMapFragment
    val client = OkHttpClient()

    val handler = Handler(Looper.myLooper()!!, Handler.Callback { msg ->
        if(msg.what == SUCCESS){
            val pathJson = msg.obj as JSONArray
            val pathList = mutableListOf<Pair<Double, Double>>()
            for(i in 0 until pathJson.length()){
                val curCoord = pathJson.getJSONObject(i)
                val coordPair = Pair(curCoord.getDouble("latitude"), curCoord.getDouble("longitude"))
                pathList.add(coordPair)
            }
            path = pathList
            mapFragment = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, mapFragment).commit()
            mapFragment.getMapAsync(this@MainActivity)
        } else {
            Log.d("LOAD ERROR", "Path Load")
        }
        true
    })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskPool.execute {
            val message = Message()
            val request = Request.Builder()
                .url("https://www.theappsdr.com/map/route")
                .build()
            try{
                val response = client.newCall(request).execute()
                message.what = SUCCESS
                val resJson = JSONObject(response.body!!.string())
                message.obj = resJson.getJSONArray("path")
            } catch (e:IOException){
                Log.d("RUN ERROR", "onCreate: $e")
            }
            handler.sendMessage(message)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        val latlngPath = path.map { LatLng(it.first, it.second) }
        val boundsBuilder = LatLngBounds.Builder()
        for(p in latlngPath) {
            boundsBuilder.include(p)
        }
        val bounds = boundsBuilder.build()
        googleMap.addPolyline(PolylineOptions()
            .clickable(true).addAll(latlngPath))
        googleMap.addMarker(MarkerOptions()
            .position(latlngPath[0])
            .title(getString(R.string.start)))
        googleMap.addMarker(MarkerOptions()
            .position(latlngPath.last())
            .title(getString(R.string.finish)))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, window.decorView.width, window.decorView.height, 100))
    }
}