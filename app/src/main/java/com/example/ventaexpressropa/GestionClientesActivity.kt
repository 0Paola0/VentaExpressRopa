package com.example.ventaexpressropa

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager            // Para el RecyclerView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot                 // Para leer nodos
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener          // Listener en tiempo real
import java.util.UUID

class GestionClientesActivity : AppCompatActivity() {

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    // Vistas de entrada
    private lateinit var editTextNombreCliente: TextInputEditText
    private lateinit var editTextCorreoCliente:  TextInputEditText
    private lateinit var editTextTelefonoCliente: TextInputEditText
    private lateinit var botonGuardarCliente:      MaterialButton

    // RecyclerView y Adapter
    private lateinit var recyclerViewClientes: RecyclerView
    private val listaClientes = mutableListOf<Cliente>()        // Dataset mutable
    private lateinit var clientesAdapter: ClientesAdapter       // Adapter personalizado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_clientes)

        // Inicializar Firebase Auth y Database
        auth     = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Vincular vistas de entrada
        editTextNombreCliente   = findViewById(R.id.edit_text_nombre_cliente)
        editTextCorreoCliente   = findViewById(R.id.edit_text_correo_cliente)
        editTextTelefonoCliente = findViewById(R.id.edit_text_telefono_cliente)
        botonGuardarCliente     = findViewById(R.id.boton_guardar_cliente)

        // Configurar RecyclerView y Adapter
        recyclerViewClientes = findViewById(R.id.recycler_view_clientes)
        clientesAdapter      = ClientesAdapter(listaClientes)     // Instanciar adapter con la lista
        recyclerViewClientes.apply {
            layoutManager = LinearLayoutManager(this@GestionClientesActivity)
            adapter       = clientesAdapter
        }

        // Listener para guardar un nuevo cliente
        botonGuardarCliente.setOnClickListener {
            guardarCliente()
        }

        // Carga inicial de clientes en tiempo real
        cargarClientesDeFirebase()
    }

    /**
     * Lee del formulario, valida, crea un Cliente y lo guarda en Firebase.
     */
    private fun guardarCliente() {
        val nombre   = editTextNombreCliente.text.toString().trim()
        val correo   = editTextCorreoCliente.text.toString().trim()
        val telefono = editTextTelefonoCliente.text.toString().trim()

        if (nombre.isEmpty()) {
            editTextNombreCliente.error = "El nombre es obligatorio"
            return
        }
        if (correo.isEmpty()) {
            editTextCorreoCliente.error = "El correo es obligatorio"
            return
        }
        if (telefono.isEmpty()) {
            editTextTelefonoCliente.error = "El teléfono es obligatorio"
            return
        }

        // Preparamos el objeto Cliente
        val nuevoCliente = Cliente(
            id       = UUID.randomUUID().toString(),
            nombre   = nombre,
            correo   = correo,
            telefono = telefono
        )

        // UID del empleado autenticado
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // Referencia y guardado en Realtime Database
        val ref = database.getReference("usuarios/$uid/clientes/${nuevoCliente.id}")
        ref.setValue(nuevoCliente)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Cliente '${nuevoCliente.nombre}' guardado",
                    Toast.LENGTH_SHORT
                ).show()
                // Limpiar formulario
                editTextNombreCliente.text?.clear()
                editTextCorreoCliente.text?.clear()
                editTextTelefonoCliente.text?.clear()
                editTextNombreCliente.requestFocus()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    "Error al guardar: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    /**
     * Coloca un ValueEventListener sobre /usuarios/{uid}/clientes para
     * mantener actualizada la lista de clientes en el RecyclerView.
     */
    private fun cargarClientesDeFirebase() {
        val uid = auth.currentUser?.uid ?: return
        val ref = database.getReference("usuarios/$uid/clientes")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaClientes.clear()  // Limpiar lista antes de recargar
                for (child in snapshot.children) {
                    val cliente = child.getValue(Cliente::class.java)
                    if (cliente != null) {
                        listaClientes.add(cliente)
                    }
                }
                clientesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@GestionClientesActivity,
                    "Error al leer clientes: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}