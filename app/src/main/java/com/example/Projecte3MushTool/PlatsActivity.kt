package com.example.Projecte3MushTool

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.Projecte3MushTool.R
import com.example.Projecte3MushTool.databinding.MapaActivityBinding
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.io.IOException
import java.nio.charset.Charset

class PlatsActivity : ComponentActivity(), MapListener {

    private lateinit var mMap: MapView
    private lateinit var controller: IMapController
    lateinit var mMyLocationOverlay: MyLocationNewOverlay;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MapaActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )
        mMap = binding.osmmap
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.mapCenter
        mMap.setMultiTouchControls(true)
        mMap.getLocalVisibleRect(Rect())

        controller = mMap.controller

        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mMap)
        controller = mMap.controller


        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true

        mMyLocationOverlay.runOnFirstFix {
            runOnUiThread {
                controller.setCenter(mMyLocationOverlay.myLocation)
                controller.animateTo(mMyLocationOverlay.myLocation)
            }
        }
        
        mMap.overlays.add(mMyLocationOverlay)
        // Leer marcadores desde el archivo JSON
        val json = loadJSONFromAsset("marcadores.json")
        if (json != null) {
            try {
                val jsonObject = JSONObject(json)
                val elements = jsonObject.getJSONArray("elements")
                for (i in 0 until elements.length()) {
                    val element = elements.getJSONObject(i)
                    val latitude = element.getDouble("lat")
                    val longitude = element.getDouble("lon")
                    val title = element.getJSONObject("tags").optString("name", "")
                    val markerLocation = GeoPoint(latitude, longitude)
                    val marker = Marker(mMap)
                    marker.position = markerLocation
                    marker.title = title
                    mMap.overlays.add(marker)
                }
            } catch (e: Exception) {
                Log.e("PlatsActivity", "Error parsing JSON", e)
            }
        }

        controller.setZoom(6.0)

        mMap.addMapListener(this)
    }

    // Función para leer un archivo JSON desde los recursos
    private fun loadJSONFromAsset(fileName: String): String? {
        var json: String? = null
        try {
            val inputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return json
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        Log.e("TAG", "onCreate:la ${event?.source?.mapCenter?.latitude}")
        Log.e("TAG", "onCreate:lo ${event?.source?.mapCenter?.longitude}")
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        Log.e("TAG", "onZoom zoom level: ${event?.zoomLevel}   source:  ${event?.source}")
        return false
    }
}
