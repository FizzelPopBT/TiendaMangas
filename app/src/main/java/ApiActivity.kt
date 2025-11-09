package com.example.tiendamanga

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ApiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listView = ListView(this)
        setContentView(listView)

        val api = provideApi(this)

        lifecycleScope.launch {
            try {
                val items = api.getProducts()
                val rows = items.map { "• ${it.nombre} — $${it.precio} (stock: ${it.stock})" }

                listView.adapter = ArrayAdapter(
                    this@ApiActivity,
                    android.R.layout.simple_list_item_1,
                    rows
                )
            } catch (e: Exception) {
                Toast.makeText(this@ApiActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


