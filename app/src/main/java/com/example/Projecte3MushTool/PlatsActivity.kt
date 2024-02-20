import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.Projecte3MushTool.R
import com.example.Projecte3MushTool.databinding.MapaActivityBinding
import okhttp3.*
import org.json.JSONArray
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.io.InputStream
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.nio.charset.Charset

class PlatsActivity : ComponentActivity(){
    /*
        lateinit var mMap: MapView
        lateinit var controller: IMapController
*/
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val binding = MapaActivityBinding.inflate(layoutInflater)
            setContentView(binding.root)
        /*
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

            // Leer marcadores desde el archivo JSON
            val json = loadJSONFromAsset("marcadores.json")
            if (json != null) {
                try {
                    val jsonArray = JSONArray(json)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val latitude = jsonObject.getDouble("latitude")
                        val longitude = jsonObject.getDouble("longitude")
                        val title = jsonObject.getString("title")
                        val markerLocation = GeoPoint(latitude, longitude)
                        val marker = Marker(mMap)
                        marker.position = markerLocation
                        marker.title = title
                        mMap.overlays.add(marker)
                    }
                } catch (e: Exception) {
                    Log.e("MapaActivity", "Error parsing JSON", e)
                }
            }

            controller.setZoom(6.0)

            mMap.addMapListener(this)*/
        }
/*
        // Función para leer un archivo JSON desde los recursos
        private fun loadJSONFromAsset(fileName: String): String? {
            var json: String? = null
            try {
                val inputStream: InputStream = assets.open(fileName)
                val size: Int = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                json = String(buffer, Charset.forName("UTF-8"))
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
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
            return false;
        }*/
}
