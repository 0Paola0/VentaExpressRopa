package com.example.ventaexpressropa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class NuevoProductoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etStock: EditText
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_producto)

        etNombre = findViewById(R.id.etNombre)
        etDescripcion = findViewById(R.id.etDescripcion)
        etPrecio = findViewById(R.id.etPrecio)
        etStock = findViewById(R.id.etStock)
        btnGuardar = findViewById(R.id.btnGuardar)

        btnGuardar.setOnClickListener { guardarProducto() }
    }

    private fun guardarProducto() {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        val id = ref.push().key ?: return

        val producto = Producto(
            id = id,
            nombre = etNombre.text.toString(),
            descripcion = etDescripcion.text.toString(),
            precio = etPrecio.text.toString(),
            stock = etStock.text.toString().toIntOrNull() ?: 0
        )

        ref.child(id).setValue(producto).addOnSuccessListener {
            Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
