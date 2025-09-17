package com.example.ventaexpressropa

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductoDetalleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto_detalle)

        val ivProducto = findViewById<ImageView>(R.id.ivDetalleProducto)
        val tvMarca = findViewById<TextView>(R.id.tvDetalleMarca)
        val tvNombre = findViewById<TextView>(R.id.tvDetalleNombre)
        val tvPrecio = findViewById<TextView>(R.id.tvDetallePrecio)
        val tvDescripcion = findViewById<TextView>(R.id.tvDetalleDescripcion)

        val marca = intent.getStringExtra("marca")
        val nombre = intent.getStringExtra("nombre")
        val precio = intent.getStringExtra("precio")
        val descripcion = intent.getStringExtra("descripcion")
        val imageResId = intent.getIntExtra("imageResId", 0)

        tvMarca.text = marca
        tvNombre.text = nombre
        tvPrecio.text = precio
        tvDescripcion.text = descripcion

        // Cargar la imagen directamente usando el ID de recurso
        ivProducto.setImageResource(imageResId)
    }
}


