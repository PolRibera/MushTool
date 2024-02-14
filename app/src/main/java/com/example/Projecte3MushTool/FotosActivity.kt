package com.example.Projecte3MushTool

import android.content.Intent
import android.os.Bundle
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class FotosActivity : ComponentActivity() {

    lateinit var imagen: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fotos_activity)
        val captura: Button = findViewById(R.id.btnFoto)
        imagen = findViewById(R.id.imagenPhoto)
        captura.setOnClickListener {
            takePhoto()
        }
        val volver: Button = findViewById(R.id.btnVolver)
        volver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    val getAction = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val bitmap = it.data?.extras?.get("data") as Bitmap
        imagen.setImageBitmap(bitmap)
    }

    fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            getAction.launch(intent)
        }
    }



}





