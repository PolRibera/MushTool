package com.example.Projecte3MushTool

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Rect
import android.location.GpsStatus
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.Projecte3MushTool.R
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import com.example.Projecte3MushTool.databinding.MapaActivityBinding
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker


class MapaActivity : AppCompatActivity(), MapListener, GpsStatus.Listener {


    lateinit var mMap: MapView
    lateinit var controller: IMapController;
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


        //marcador
        controller.setZoom(6.0)
        val markerLocation = GeoPoint(41.3851, 2.1734)
        val marker = Marker(mMap)
        marker.position = markerLocation
        marker.title = "Barcelona"
        mMap.overlays.add(marker)
        mMap.overlays.add(mMyLocationOverlay)

        Log.e("TAG", "onCreate:in ${controller.zoomIn()}")
        Log.e("TAG", "onCreate: out  ${controller.zoomOut()}")

        controller.setZoom(6.0)

        mMap.addMapListener(this)
    }



    override fun onScroll(event: ScrollEvent?): Boolean {
        Log.e("TAG", "onCreate:la ${event?.source?.mapCenter?.latitude}")
        Log.e("TAG", "onCreate:lo ${event?.source?.mapCenter?.longitude}")
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        Log.e("TAG", "onZoom zoom level: ${event?.zoomLevel}   source:  ${event?.source}")
        return false;
    }

    override fun onGpsStatusChanged(p0: Int) {
        TODO("No esta implementado")
    }


}