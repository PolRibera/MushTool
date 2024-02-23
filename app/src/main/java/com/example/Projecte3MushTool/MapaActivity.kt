package com.example.Projecte3MushTool
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.database.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapaActivity : ComponentActivity() {
    private lateinit var postReference: DatabaseReference
    lateinit var mMyLocationOverlay: MyLocationNewOverlay;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar la instancia de Firebase
        postReference = FirebaseDatabase.getInstance().getReference("Post")

        // Cargar la vista del mapa
        setContent {
            MapScreen(this)
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MapScreen(context: Context) {
        Configuration.getInstance().load(applicationContext, androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext))
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.background(Color(0xFF6B0C0C)),
                    title = { },
                    actions = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF6B0C0C)),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Mushtool",
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.CenterVertically),
                                color = Color.White
                            )

                            Button(
                                onClick = {
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(Color(0xFF6B0C0C)),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .background(Color(0xFF6B0C0C))
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.boton_exit),
                                    contentDescription = "Exit Button",
                                    modifier = Modifier.size(30.dp, 30.dp)
                                )
                            }
                        }
                    }
                )
            }
        ) {
            MapViewContainer()
        }
    }

    @Composable
    fun MapViewContainer() {
        val mapView = MapView(LocalContext.current).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }
        MapViewContent(mapView)
    }

    @Composable
    fun MapViewContent(mapView: MapView) {
        FirebasePostListener(mapView,this)

        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize().padding(8.dp)
        )
    }

    @Composable
    fun FirebasePostListener(mapView: MapView, context: Context) {
        // Listener de Firebase para obtener los posts y agregar marcadores al mapa
        postReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val points = mutableListOf<GeoPoint>()
                val mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
                mMyLocationOverlay.enableMyLocation()
                mMyLocationOverlay.enableFollowLocation()
                mMyLocationOverlay.isDrawAccuracyEnabled = true

                mMyLocationOverlay.runOnFirstFix {
                    (context as ComponentActivity).runOnUiThread {
                        mapView.controller.setCenter(mMyLocationOverlay.myLocation)
                        mapView.controller.animateTo(mMyLocationOverlay.myLocation)
                    }
                }
                mapView.overlays.add(mMyLocationOverlay)
                for (postSnapshot in dataSnapshot.children) {
                    val locationString = postSnapshot.child("location").getValue(String::class.java)

                    if (locationString != null) {
                        val (latitude, longitude) = locationString.split(";")
                        val lat = latitude.toDouble()
                        val lon = longitude.toDouble()

                        // Agregar marcador al mapa
                        val location = GeoPoint(lat, lon)
                        val marker = Marker(mapView)
                        marker.position = location
                        mapView.overlays.add(marker)

                        points.add(location)
                    }
                }


                if (points.isNotEmpty()) {
                    // Calcular el nivel de zoom necesario para mostrar todos los puntos
                    val metersPerPixel = calculateMetersPerPixel(mapView)
                    val zoomLevel = calculateZoomLevel(metersPerPixel, mapView.width)
                    mapView.controller.setZoom(zoomLevel)

                }

                // Refrescar el mapa para que se muestren los marcadores
                mapView.invalidate()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar el error de Firebase
            }
        })
    }


    fun calculateZoomLevel(distancePerPixel: Double, screenWidth: Int): Double {
        // Ajusta estos valores según sea necesario para obtener el zoom deseado
        val desiredVisibleDistance = 7000000 // Distancia en metros
        val zoomCoefficient = 256 // Valor fijo para osmdroid

        // Calcula la distancia visible en el mapa
        val visibleDistance = screenWidth * distancePerPixel

        // Calcula el nivel de zoom necesario para que la distancia visible sea igual a la deseada
        return Math.log(desiredVisibleDistance * zoomCoefficient / visibleDistance) / Math.log(2.0)
    }

    fun calculateVisibleRegion(mapView: MapView): BoundingBox {
        val topLeft = mapView.projection.fromPixels(0, 0)
        val bottomRight = mapView.projection.fromPixels(mapView.width, mapView.height)
        return BoundingBox(bottomRight.latitude, topLeft.longitude, topLeft.latitude, bottomRight.longitude)
    }

    fun calculateMetersPerPixel(mapView: MapView): Double {
        val bounds = calculateVisibleRegion(mapView)

        val left = GeoPoint(bounds.latSouth, bounds.lonWest)
        val right = GeoPoint(bounds.latSouth, bounds.lonEast)

        val widthInMeters = left.distanceToAsDouble(right)
        val widthInPixels = mapView.width

        return widthInMeters / widthInPixels
    }



}
