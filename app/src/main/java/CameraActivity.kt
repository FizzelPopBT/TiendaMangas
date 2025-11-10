package com.example.tiendamanga

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class CameraActivity : ComponentActivity() {

    private val prefs by lazy { getSharedPreferences("tiendamanga", MODE_PRIVATE) }
    private var pendingUri: Uri? = null

    private lateinit var grid: GridView
    private lateinit var adapter: ArrayAdapter<String>

    private val takePicture = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingUri != null) {
            val u = pendingUri!!.toString()
            addUri(u)
            Toast.makeText(this, "Foto guardada", Toast.LENGTH_SHORT).show()
            refreshGrid()
        } else {
            Toast.makeText(this, "Captura cancelada", Toast.LENGTH_SHORT).show()
        }
        pendingUri = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Paleta MangaZone
        val bg = Color.parseColor("#0E0B12")
        val accent = Color.parseColor("#B97AFF")
        val textMain = Color.WHITE
        val soft = Color.parseColor("#BEBEBE")

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            val p = (20 * resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }

        val title = TextView(this).apply {
            text = "Cámara — TiendaManga"
            setTextColor(accent)
            textSize = 22f
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val btnTake = Button(this).apply {
            text = "Tomar foto"
            setTextColor(Color.WHITE)
            setBackgroundColor(accent)
            setOnClickListener { capturePhoto() }
        }

        val space = Space(this).apply { minimumWidth = (12 * resources.displayMetrics.density).toInt() }

        val btnShareLast = Button(this).apply {
            text = "Compartir última"
            setTextColor(Color.WHITE)
            setBackgroundColor(accent)
            setOnClickListener { shareLast() }
        }

        val btnClear = Button(this).apply {
            text = "Limpiar lista (app)"
            setTextColor(soft)
            setBackgroundColor(Color.TRANSPARENT)
            setOnClickListener {
                saveUris(emptyList())
                refreshGrid()
                Toast.makeText(this@CameraActivity, "Lista interna borrada", Toast.LENGTH_SHORT).show()
            }
        }

        grid = GridView(this).apply {
            numColumns = 3
            verticalSpacing = (8 * resources.displayMetrics.density).toInt()
            horizontalSpacing = (8 * resources.displayMetrics.density).toInt()
            stretchMode = GridView.STRETCH_COLUMN_WIDTH
        }

        adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, loadUris()) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val image = ImageView(this@CameraActivity)
                val size = parent.width / 3 - (8 * resources.displayMetrics.density).toInt()
                image.layoutParams = AbsListView.LayoutParams(size, size)
                image.scaleType = ImageView.ScaleType.CENTER_CROP
                image.setImageURI(Uri.parse(getItem(position)))
                return image
            }
        }
        grid.adapter = adapter

        grid.setOnItemClickListener { _, _, pos, _ ->
            val uri = Uri.parse(adapter.getItem(pos))
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(intent)
        }

        root.addView(title)
        val topSpace = Space(this).apply { minimumHeight = (12 * resources.displayMetrics.density).toInt() }
        root.addView(topSpace)

        row.addView(btnTake)
        row.addView(space)
        row.addView(btnShareLast)
        root.addView(row)

        val midSpace = Space(this).apply { minimumHeight = (8 * resources.displayMetrics.density).toInt() }
        root.addView(midSpace)

        root.addView(btnClear)
        val spacer = Space(this).apply { minimumHeight = (8 * resources.displayMetrics.density).toInt() }
        root.addView(spacer)

        root.addView(grid)
        setContentView(root)

        refreshGrid()
    }

    private fun capturePhoto() {
        val name = "producto_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TiendaManga")
            }
        }
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            else
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        pendingUri = contentResolver.insert(collection, values)
        if (pendingUri == null) {
            Toast.makeText(this, "No se pudo crear el archivo", Toast.LENGTH_LONG).show()
            return
        }
        takePicture.launch(pendingUri)
    }

    private fun shareLast() {
        val list = loadUris()
        if (list.isEmpty()) {
            Toast.makeText(this, "Aún no tienes fotos", Toast.LENGTH_SHORT).show()
            return
        }
        val uri = Uri.parse(list.last())
        val share = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(share, "Compartir foto"))
    }

    private fun loadUris(): MutableList<String> {
        val str = prefs.getString("fotos_uris", "") ?: ""
        if (str.isBlank()) return mutableListOf()
        return str.split("|").filter { it.isNotBlank() }.toMutableList()
    }

    private fun saveUris(list: List<String>) {
        prefs.edit().putString("fotos_uris", list.joinToString("|")).apply()
    }

    private fun addUri(u: String) {
        val list = loadUris()
        list.add(u)
        saveUris(list)
    }

    private fun refreshGrid() {
        adapter.clear()
        adapter.addAll(loadUris())
        adapter.notifyDataSetChanged()
    }
}

