package com.example.tiendamanga

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class CameraActivity : ComponentActivity() {

    private var photoUri: Uri? = null

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Muestra la foto
            imageView.setImageURI(photoUri)
            // Guarda ruta en persistencia local (SharedPreferences)
            val prefs = getSharedPreferences("tiendamanga", MODE_PRIVATE)
            prefs.edit().putString("ultima_foto_uri", photoUri.toString()).apply()
            Toast.makeText(this, "Foto guardada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageView = ImageView(this)
        val btn = Button(this).apply { text = "Tomar foto" }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val p = 30; setPadding(p,p,p,p)
            addView(btn)
            addView(imageView)
        }
        setContentView(layout)

        btn.setOnClickListener {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "producto_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
            val resolver = contentResolver
            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            photoUri = resolver.insert(collection, values)
            if (photoUri != null) {
                takePicture.launch(photoUri)
            } else {
                Toast.makeText(this, "No se pudo crear el archivo", Toast.LENGTH_SHORT).show()
            }
        }

        val saved = getSharedPreferences("tiendamanga", MODE_PRIVATE)
            .getString("ultima_foto_uri", null)
        if (saved != null) imageView.setImageURI(Uri.parse(saved))
    }
}
