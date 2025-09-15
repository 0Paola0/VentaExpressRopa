package com.example.ventaexpressropa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegistroActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etConfPass: EditText
    private lateinit var etPass: EditText
    private lateinit var btnSignUp: Button
    lateinit var tvRedirectLogin: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        etEmail = findViewById(R.id.campo_correo)
        etConfPass = findViewById(R.id.campo_contraseña)
        etPass = findViewById(R.id.campo_confirmar_contraseña)
        btnSignUp = findViewById(R.id.boton_registrarse)
        tvRedirectLogin = findViewById(R.id.texto_ya_tienes_cuenta)
        auth = Firebase.auth

        btnSignUp.setOnClickListener {
            signUpUser()
        }
        tvRedirectLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    private fun signUpUser() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        val confirmPassword = etConfPass.text.toString()
        if (email.isBlank() || pass.isBlank() ||
            confirmPassword.isBlank()) {
            Toast.makeText(this, "Rellenar todos los campos",
                Toast.LENGTH_SHORT).show()
            return
        }
        if (pass != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden",
                Toast.LENGTH_SHORT).show()
            return
}
        auth.createUserWithEmailAndPassword(email,
            pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Registro exitoso",
                    Toast.LENGTH_SHORT).show()
                val intent=Intent(this,LoginActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Registro fallido",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}
