package com.example.ventaexpressropa

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistorialVentasActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var recyclerVentas: RecyclerView
    private val ventas = mutableListOf<Venta>()
    private lateinit var ventasAdapter: HistorialVentasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_ventas)

        database = FirebaseDatabase.getInstance()
        recyclerVentas = findViewById(R.id.recycler_ventas)

        ventasAdapter = HistorialVentasAdapter(ventas)
        recyclerVentas.layoutManager = LinearLayoutManager(this)
        recyclerVentas.adapter = ventasAdapter

        cargarVentas()
    }

    private fun cargarVentas() {
        val ref = database.getReference("Ventas")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ventas.clear()
                for (ventaSnapshot in snapshot.children) {
                    val venta = ventaSnapshot.getValue(Venta::class.java)
                    if (venta != null) {
                        ventas.add(venta)
                    }
                }
                // Ordenar por fecha (más recientes primero)
                ventas.sortByDescending { it.fechaIso8601 }
                ventasAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistorialVentasActivity, "Error al cargar ventas: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
