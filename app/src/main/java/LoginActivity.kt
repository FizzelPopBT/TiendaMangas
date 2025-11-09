package com.example.tiendamanga

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*
import androidx.activity.ComponentActivity

class LoginActivity : ComponentActivity() {

    private val VALID_EMAIL = "Admin@mangastore.cl"
    private val VALID_PASS  = "Admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("tiendamanga", MODE_PRIVATE)
        if (prefs.getBoolean("logged_in", false)) {
            goHome()
            return
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val p = (48 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val title = TextView(this).apply {
            text = "Iniciar sesión"
            textSize = 22f
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val email = EditText(this).apply {
            hint = "Correo"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }

        val pass = EditText(this).apply {
            hint = "Contraseña"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val error = TextView(this).apply {
            text = ""
            setTextColor(0xFFCC0000.toInt()) // rojo
        }

        val btn = Button(this).apply {
            text = "Entrar"
            setOnClickListener {
                val ok = validate(email.text.toString().trim(), pass.text.toString())
                if (ok) {
                    prefs.edit().putBoolean("logged_in", true).apply()
                    goHome()
                } else {
                    error.text = "Usuario o contraseña incorrectos"
                    Toast.makeText(this@LoginActivity, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                }
            }
        }

        root.addView(title)
        root.addView(spacer())
        root.addView(email)
        root.addView(pass)
        root.addView(spacer(8))
        root.addView(btn)
        root.addView(spacer(8))
        root.addView(error)
        setContentView(root)
    }

    private fun validate(email: String, pass: String): Boolean {
        return email == VALID_EMAIL && pass == VALID_PASS
    }

    private fun goHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun spacer(dp: Int = 16): Space {
        val s = Space(this)
        s.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            (dp * resources.displayMetrics.density).toInt()
        )
        return s
    }
}
