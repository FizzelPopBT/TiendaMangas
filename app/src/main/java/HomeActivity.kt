package com.example.tiendamanga

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Layout vertical simple
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val p = (48 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }

        // Título
        val title = TextView(this).apply {
            text = "Bienvenido a TiendaManga"
            textSize = 20f
        }

        // Botones
        val btnTienda = Button(this).apply { text = "Ver tienda" }
        val btnCamara = Button(this).apply { text = "Abrir cámara" }
        val btnApi = Button(this).apply { text = "Ver API productos" }
        val btnLogout = Button(this).apply { text = "Cerrar sesión" }

        // Agregar al layout
        layout.addView(title)
        layout.addView(btnTienda)
        layout.addView(btnCamara)
        layout.addView(btnApi)
        layout.addView(btnLogout)

        setContentView(layout)

        // Acciones de los botones
        btnTienda.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnCamara.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        btnApi.setOnClickListener {
            startActivity(Intent(this, ApiActivity::class.java))
        }

        btnLogout.setOnClickListener {
            // Borra sesión y vuelve al login
            getSharedPreferences("tiendamanga", MODE_PRIVATE)
                .edit().putBoolean("logged_in", false).apply()
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }
    }
}

