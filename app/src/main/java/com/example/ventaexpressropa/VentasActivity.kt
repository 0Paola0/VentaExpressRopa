package com.example.ventaexpressropa

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class VentasActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var selectorCliente: AutoCompleteTextView
    private lateinit var recyclerProductosVenta: RecyclerView
    private lateinit var textoTotal: TextView
    private lateinit var botonAgregarProducto: Button
    private lateinit var botonGuardarVenta: Button

    private val clientes = mutableListOf<Cliente>()
    private lateinit var clientesAdapter: ArrayAdapter<String>

    private val items = mutableListOf<VentaProductoItem>()
    private val catalogo = mutableListOf<CatalogoProducto>()
    private lateinit var ventaItemsAdapter: VentasItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventas)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        selectorCliente = findViewById(R.id.autocomplete_cliente)
        recyclerProductosVenta = findViewById(R.id.recycler_items_venta)
        textoTotal = findViewById(R.id.text_total)
        botonAgregarProducto = findViewById(R.id.btn_agregar_producto)
        botonGuardarVenta = findViewById(R.id.btn_guardar_venta)

        clientesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())
        selectorCliente.setAdapter(clientesAdapter)
        selectorCliente.threshold = 1
        selectorCliente.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && clientesAdapter.count > 0) selectorCliente.showDropDown()
        }
        selectorCliente.setOnClickListener {
            if (clientesAdapter.count > 0) selectorCliente.showDropDown()
        }

        ventaItemsAdapter = VentasItemsAdapter(items) { actualizarTotal() }
        recyclerProductosVenta.layoutManager = LinearLayoutManager(this)
        recyclerProductosVenta.adapter = ventaItemsAdapter

        botonAgregarProducto.setOnClickListener { mostrarDialogoAgregarProducto() }
        botonGuardarVenta.setOnClickListener { guardarVenta() }

        cargarClientesDeTodosLosUsuarios()
        cargarProductosDesdeTopLevel()
        actualizarTotal()
    }

    private fun cargarProductosDesdeTopLevel() {
        val ref = database.getReference("Productos")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                catalogo.clear()
                var agregados = 0
                for (p in snapshot.children) {
                    val nombre = p.child("nombre").getValue(String::class.java) ?: ""
                    val marca = p.child("marca").getValue(String::class.java) ?: ""
                    val precioStr = (p.child("precio").getValue(String::class.java)
                        ?: p.child("precio").getValue(Number::class.java)?.toString()
                        ?: "0")
                    val precio = precioStr.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0
                    if (nombre.isNotEmpty()) {
                        catalogo.add(CatalogoProducto(nombre = nombre, marca = marca, precio = precio))
                        agregados++
                    }
                }
                Toast.makeText(this@VentasActivity, "Productos cargados: $agregados", Toast.LENGTH_SHORT).show()
                if (esperandoCatalogoParaAbrir && catalogo.isNotEmpty()) {
                    esperandoCatalogoParaAbrir = false
                    mostrarDialogoAgregarProducto()
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VentasActivity, "Error cargando productos: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun cargarProductosTopLevelMinuscula() {
        val ref = database.getReference("productos")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (p in snapshot.children) {
                    val nombre = p.child("nombre").getValue(String::class.java) ?: ""
                    val marca = p.child("marca").getValue(String::class.java) ?: ""
                    val precioStr = (p.child("precio").getValue(String::class.java)
                        ?: p.child("precio").getValue(Number::class.java)?.toString()
                        ?: "0")
                    val precio = precioStr.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0
                    if (nombre.isNotEmpty()) {
                        catalogo.add(CatalogoProducto(nombre = nombre, marca = marca, precio = precio))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }

    private fun cargarProductosDeTodosLosUsuarios() {
        val raiz = database.getReference("usuarios")
        raiz.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                catalogo.clear()
                for (usuario in snapshot.children) {
                    val productosNode = usuario.child("productos")
                    for (p in productosNode.children) {
                        val marca = p.child("marca").getValue(String::class.java) ?: ""
                        val nombre = p.child("nombre").getValue(String::class.java) ?: ""
                        val precioStr = p.child("precio").getValue(String::class.java) ?: "0"
                        val precio = precioStr.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0
                        if (nombre.isNotEmpty()) {
                            catalogo.add(CatalogoProducto(nombre = nombre, marca = marca, precio = precio))
                        }
                    }
                }
                if (esperandoCatalogoParaAbrir && catalogo.isNotEmpty()) {
                    esperandoCatalogoParaAbrir = false
                    mostrarDialogoAgregarProducto()
                }
                // Fallback si no hay nada global: intentar bajo el uid actual
                if (catalogo.isEmpty()) cargarProductosPorUid()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VentasActivity, "Error al cargar productos: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun cargarProductosPorUid() {
        val uid = auth.currentUser?.uid ?: return
        val ref = database.getReference("usuarios/$uid/productos")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                catalogo.clear()
                for (p in snapshot.children) {
                    // El modelo existente Producto tiene precio como String; intentamos parsear
                    val marca = p.child("marca").getValue(String::class.java) ?: ""
                    val nombre = p.child("nombre").getValue(String::class.java) ?: ""
                    val precioStr = p.child("precio").getValue(String::class.java) ?: "0"
                    val precio = precioStr.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0
                    if (nombre.isNotEmpty()) {
                        catalogo.add(CatalogoProducto(nombre = nombre, marca = marca, precio = precio))
                    }
                }
                if (esperandoCatalogoParaAbrir && catalogo.isNotEmpty()) {
                    esperandoCatalogoParaAbrir = false
                    mostrarDialogoAgregarProducto()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VentasActivity, "Error al cargar productos: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun cargarClientesDeTodosLosUsuarios() {
        val raiz = database.getReference("usuarios")
        raiz.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clientes.clear()
                val nombres = mutableListOf<String>()
                for (usuario in snapshot.children) {
                    val clientesNode = usuario.child("clientes")
                    for (c in clientesNode.children) {
                        val cli = c.getValue(Cliente::class.java)
                        if (cli != null) {
                            clientes.add(cli)
                            if (cli.nombre.isNotEmpty()) nombres.add(cli.nombre)
                        }
                    }
                }
                if (nombres.isEmpty()) {
                    cargarClientesPorUid()
                    return
                }
                clientesAdapter.clear()
                clientesAdapter.addAll(nombres.distinct())
                clientesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VentasActivity, "Error al cargar clientes: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun cargarClientesPorUid() {
        val uid = auth.currentUser?.uid ?: return
        val ref = database.getReference("usuarios/$uid/clientes")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                clientes.clear()
                val nombres = mutableListOf<String>()
                for (c in snapshot.children) {
                    val cli = c.getValue(Cliente::class.java)
                    if (cli != null) {
                        clientes.add(cli)
                        nombres.add(cli.nombre)
                    }
                }
                clientesAdapter.clear()
                clientesAdapter.addAll(nombres)
                clientesAdapter.notifyDataSetChanged()
                if (esperandoCatalogoParaAbrir && catalogo.isNotEmpty()) {
                    esperandoCatalogoParaAbrir = false
                    mostrarDialogoAgregarProducto()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VentasActivity, "Error al cargar clientes: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private var esperandoCatalogoParaAbrir = false

    private fun mostrarDialogoAgregarProducto() {
        if (catalogo.isEmpty()) {
            Toast.makeText(this, "Cargando productos...", Toast.LENGTH_SHORT).show()
            esperandoCatalogoParaAbrir = true
            cargarProductosDesdeTopLevel()
            return
        }
        val dialog = VentasProductoDialogFragment(catalogo) { prod, cantStr ->
            val cant = cantStr.toIntOrNull() ?: 0
            val item = VentaProductoItem(
                nombreProducto = prod.nombre,
                marcaProducto = prod.marca,
                precioUnitario = prod.precio,
                cantidad = cant,
                subtotal = prod.precio * cant
            )
            items.add(item)
            ventaItemsAdapter.notifyItemInserted(items.size - 1)
            actualizarTotal()
        }
        dialog.show(supportFragmentManager, "VentasProductoDialog")
    }

    private fun actualizarTotal() {
        val total = items.sumOf { it.subtotal }
        textoTotal.text = String.format(Locale.getDefault(), "Total: $%.2f", total)
    }

    private fun guardarVenta() {
        val clienteNombre = selectorCliente.text?.toString()?.trim().orEmpty()
        if (clienteNombre.isEmpty()) {
            selectorCliente.error = "Selecciona un cliente"
            return
        }
        if (items.isEmpty()) {
            Toast.makeText(this, "Agrega al menos un producto", Toast.LENGTH_SHORT).show()
            return
        }

        val cliente = clientes.firstOrNull { it.nombre == clienteNombre }
        if (cliente == null) {
            selectorCliente.error = "Cliente inválido"
            return
        }

        val ventaId = UUID.randomUUID().toString()
        val fecha = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()).format(Date())
        val total = items.sumOf { it.subtotal }
        val venta = Venta(
            id = ventaId,
            clienteId = cliente.id,
            clienteNombre = cliente.nombre,
            fechaIso8601 = fecha,
            total = total,
            items = items.toList()
        )

        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = database.getReference("Ventas/$ventaId")
        ref.setValue(venta)
            .addOnSuccessListener {
                Toast.makeText(this, "Venta guardada", Toast.LENGTH_SHORT).show()
                items.clear()
                ventaItemsAdapter.notifyDataSetChanged()
                actualizarTotal()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}


