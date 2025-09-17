package com.example.ventaexpressropa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    
    private lateinit var btnProductos: Button
    private lateinit var btnClientes: Button
    private lateinit var btnVentas: Button
    private lateinit var btnHistorialVentas: Button
    private lateinit var saludoUsuario: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        inicializarComponentes()
        configurarBotones()
        mostrarSaludoUsuario()
    }
    
    private fun inicializarComponentes() {
        btnProductos = findViewById(R.id.boton_productos)
        btnClientes = findViewById(R.id.boton_clientes)
        btnVentas = findViewById(R.id.boton_ventas)
        btnHistorialVentas = findViewById(R.id.boton_historial_ventas)
        saludoUsuario = findViewById(R.id.saludo_usuario)
        auth = FirebaseAuth.getInstance()
    }

    private fun configurarBotones() {
        btnProductos.setOnClickListener {
            Log.d("MainActivity", "Botón Productos presionado")
            Toast.makeText(this, "Abriendo gestión de productos...", Toast.LENGTH_SHORT).show()

            // Abrir la Activity de productos
            val intent = Intent(this, ProductosActivity::class.java)
            startActivity(intent)
        }





        btnClientes.setOnClickListener {
            Log.d("MainActivity", "Botón Clientes presionado") // Cambiado el Tag para consistencia
            Toast.makeText(this, "Abriendo gestión de clientes...", Toast.LENGTH_SHORT).show()

            // --- INICIO DE LA MODIFICACIÓN ---
            // Crea un Intent para iniciar GestionClientesActivity
            val intent = Intent(this, GestionClientesActivity::class.java)
            startActivity(intent)
            // --- FIN DE LA MODIFICACIÓN ---
        }


        btnVentas.setOnClickListener {
            Log.d("MainActivity", "Botón Ventas presionado")
            Toast.makeText(this, "Abriendo gestión de ventas...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, VentasActivity::class.java)
            startActivity(intent)
        }

        btnHistorialVentas.setOnClickListener {
            Log.d("MainActivity", "Botón Historial Ventas presionado")
            Toast.makeText(this, "Abriendo historial de ventas...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HistorialVentasActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun mostrarSaludoUsuario() {
        val user = auth.currentUser
        if (user != null) {
            val nombre = user.displayName ?: "Usuario"
            saludoUsuario.text = "¡Hola, $nombre! 👋"
            Log.d("MainActivity", "Usuario autenticado: ${user.email}")
        } else {
            saludoUsuario.text = "¡Bienvenido a tu tienda! 🏪"
            Log.d("MainActivity", "Usuario no autenticado")
        }
    }

    
    override fun onResume() {
        super.onResume()
        mostrarSaludoUsuario()
    }

}