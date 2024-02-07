package com.example.Projecte3MushTool

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import com.example.lemonade.ui.theme.AppTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView


class MapaActivity : ComponentActivity() {


    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView

    // Crear el contrato para solicitar permisos
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Permiso denegado, puedes manejarlo aquí
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContent {
            AppTheme {
                MapaApp(this)
            }
        }

        map.setTileSource(TileSourceFactory.MAPNIK)
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MapaApp(context: Context) { // Define el color de los botones aquí
        Scaffold(
            topBar = {
                // Define tu TopBar aquí
                TopAppBar(
                    modifier = Modifier.background(Color(0xFF6B0C0C)),
                    title = { }, // No se muestra texto en el título de la TopAppBar
                    actions = {
                        Row(
                            modifier = Modifier.fillMaxWidth().background(Color(0xFF6B0C0C)),
                            horizontalArrangement = Arrangement.SpaceBetween // Distribuye los elementos de manera uniforme en la fila
                        ) {
                            Text("Mushtool", modifier = Modifier.padding(10.dp).align(Alignment.CenterVertically), color = Color.White) // Texto que se muestra en la esquina izquierda // Texto que se muestra en la esquina izquierda

                            Button(
                                onClick = {
                                    val intent = Intent(context, MainActivity::class.java)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    // Tamaño del botón
                                    .align(Alignment.CenterVertically)
                                    .background(Color(0xFF6B0C0C))
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.boton_exit), // Cambiar con tu recurso
                                    contentDescription = "Descripción de la imagen",
                                    modifier = Modifier
                                        .size(30.dp, 30.dp) // Tamaño de la imagen
                                    // Hace que la imagen llene todo el espacio disponible del botón
                                )
                            }
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            id = R.id.map
                            setTileSource(TileSourceFactory.MAPNIK)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly // Distribuye los elementos de manera uniforme en la fila
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Mapa") // Texto del botón
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        AppTheme {
            MapaApp(LocalContext.current)
        }
    }

}
