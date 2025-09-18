package com.example.ventaexpressropa



import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class EditarProductoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etStock: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button

    private var productoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_producto)

        etNombre = findViewById(R.id.etNombre)
        etDescripcion = findViewById(R.id.etDescripcion)
        etPrecio = findViewById(R.id.etPrecio)
        etStock = findViewById(R.id.etStock)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnEliminar = findViewById(R.id.btnEliminar)

        productoId = intent.getStringExtra("id")
        if (productoId == null) {
            Toast.makeText(this, "No se encontró el producto", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarProducto(productoId!!)

        btnGuardar.setOnClickListener { actualizarProducto() }
        btnEliminar.setOnClickListener { eliminarProducto() }
    }

    private fun cargarProducto(id: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos").child(id)
        ref.get().addOnSuccessListener { snap ->
            val p = snap.getValue(Producto::class.java)
            p?.let {
                etNombre.setText(it.nombre)
                etDescripcion.setText(it.descripcion)
                etPrecio.setText(it.precio)
                etStock.setText(it.stock.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error cargando producto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarProducto() {
        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val precio = etPrecio.text.toString().trim()
        val stock = etStock.text.toString().trim().toIntOrNull() ?: 0

        if (nombre.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Nombre y precio son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val id = productoId ?: return
        val producto = Producto(id = id, nombre = nombre, descripcion = descripcion, precio = precio, stock = stock)
        val ref = FirebaseDatabase.getInstance().getReference("Productos").child(id)

        ref.setValue(producto).addOnSuccessListener {
            Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarProducto() {
        val id = productoId ?: return
        val ref = FirebaseDatabase.getInstance().getReference("Productos").child(id)
        ref.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
        }
    }
}
