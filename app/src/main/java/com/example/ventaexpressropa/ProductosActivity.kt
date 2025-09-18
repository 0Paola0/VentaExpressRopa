package com.example.ventaexpressropa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private val productos = mutableListOf<Producto>()

    private lateinit var btnAgregar: FloatingActionButton
    private var esAdmin: Boolean = false // Aquí decides según Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        recyclerView = findViewById(R.id.recyclerProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ProductoAdapter(productos) { producto ->
            if (esAdmin) {
                // Solo el admin puede editar
                val intent = Intent(this, EditarProductoActivity::class.java)
                intent.putExtra("id", producto.id)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Solo los administradores pueden editar", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = adapter

        btnAgregar = findViewById(R.id.btnAgregarProducto)
        btnAgregar.setOnClickListener {
            if (esAdmin) {
                startActivity(Intent(this, NuevoProductoActivity::class.java))
            } else {
                Toast.makeText(this, "Solo los administradores pueden agregar productos", Toast.LENGTH_SHORT).show()
            }
        }

        cargarProductosFirebase()
        verificarRolUsuario()
    }

    private fun cargarProductosFirebase() {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productos.clear()
                for (prodSnapshot in snapshot.children) {
                    val producto = prodSnapshot.getValue(Producto::class.java)
                    producto?.let { productos.add(it) }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun verificarRolUsuario() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios/$uid")

        ref.get().addOnSuccessListener {
            val rol = it.child("rol").value.toString()
            esAdmin = rol == "admin"
            btnAgregar.visibility = if (esAdmin) View.VISIBLE else View.GONE
        }
    }
}



