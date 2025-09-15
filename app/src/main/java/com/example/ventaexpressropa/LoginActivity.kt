package com.example.ventaexpressropa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.LoginStatusCallback
import com.google.firebase.auth.FacebookAuthProvider
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRedirectSignUp: TextView
    private lateinit var btnGithub: Button
    private lateinit var btnFacebook: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnLogin = findViewById(R.id.boton_entrar)
        etEmail = findViewById(R.id.campo_correo_login)
        etPass = findViewById(R.id.campo_contraseña_login)
        btnGithub = findViewById(R.id.boton_entrar_github)
        btnFacebook = findViewById(R.id.boton_entrar_facebook)
        tvRedirectSignUp= findViewById(R.id.texto_registrate)
        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().retrieveLoginStatus(this, object : LoginStatusCallback {
            override fun onCompleted(accessToken: AccessToken) {
                Log.d("FacebookAuth", "Sesión previa encontrada. Usuario ya autenticado en Facebook")
                handleFacebookAccessToken(accessToken)
                Toast.makeText(this@LoginActivity, "Iniciando sesión como usuario de Facebook", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure() {
                Log.d("FacebookAuth", "No se pudo recuperar un token de Facebook (no hay sesión previa)")
            }

            override fun onError(exception: Exception) {
                Log.e("FacebookAuth", "Error al recuperar estado de login de Facebook: ${exception.message}")
            }
        })

        btnLogin.setOnClickListener {
            login()
        }
        
        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        btnGithub.setOnClickListener {
            Log.d("LoginActivity", "Botón de GitHub presionado")
            Toast.makeText(this, "Iniciando login con GitHub...", Toast.LENGTH_SHORT).show()
            iniciarSesionGitHub()
        }
        
        btnFacebook.setOnClickListener {
            Log.d("LoginActivity", "Botón de Facebook presionado")
            Toast.makeText(this, "Iniciando login con Facebook...", Toast.LENGTH_SHORT).show()
            iniciarSesionFacebook()
        }
    }
    private fun login(){
        try {
            Log.d("LoginActivity", "Iniciando proceso de login...")
            
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()
            
            Log.d("LoginActivity", "Email: $email")
            Log.d("LoginActivity", "Password length: ${pass.length}")
            
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return
            }
            
            Log.d("LoginActivity", "Intentando autenticación con Firebase...")
            
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    Log.d("LoginActivity", "Tarea de autenticación completada")
                    if (task.isSuccessful) {
                        Log.d("LoginActivity", "Login exitoso")
                        Toast.makeText(this, "Login Exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("LoginActivity", "Error en login: ${task.exception?.message}")
                        Toast.makeText(this, "Error con login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LoginActivity", "Error en autenticación: ${e.message}")
                    Toast.makeText(this, "Error de autenticación: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Excepción en login: ${e.message}")
            Log.e("LoginActivity", "Stack trace: ${e.stackTraceToString()}")
            Toast.makeText(this, "Error inesperado: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun iniciarSesionGitHub() {
        Log.d("GitHubAuth", "Iniciando autenticación con GitHub OAuth...")
        
        try {
            if (auth == null) {
                Log.e("GitHubAuth", "Firebase Auth no está inicializado")
                Toast.makeText(this, "Error: Firebase Auth no está configurado", Toast.LENGTH_LONG).show()
                return
            }
            
            // Configurar el proveedor OAuth de GitHub
            val provider = OAuthProvider.newBuilder("github.com")
            provider.scopes = listOf("read:user", "user:email")
            
            Log.d("GitHubAuth", "Proveedor OAuth configurado correctamente")
            Log.d("GitHubAuth", "Scopes configurados: ${provider.scopes}")

            // Verificar si hay una sesión pendiente
            val pendingResultTask = auth.pendingAuthResult
            if (pendingResultTask != null) {
                Log.d("GitHubAuth", "Completando sesión pendiente...")
                pendingResultTask
                    .addOnSuccessListener { authResult ->
                        manejarLoginExitoso(authResult.user, "GitHub")
                    }
                    .addOnFailureListener { e ->
                        manejarErrorGitHub(e)
                    }
            } else {
                Log.d("GitHubAuth", "Iniciando nueva autenticación con GitHub...")
                auth.startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener { authResult ->
                        manejarLoginExitoso(authResult.user, "GitHub")
                    }
                    .addOnFailureListener { e ->
                        manejarErrorGitHub(e)
                    }
            }
        } catch (e: Exception) {
            Log.e("GitHubAuth", "Excepción al iniciar autenticación: ${e.message}")
            Toast.makeText(this, "Error al iniciar autenticación con GitHub: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun manejarErrorGitHub(e: Exception) {
        Log.e("GitHubAuth", "Error en autenticación: ${e.message}")
        Log.e("GitHubAuth", "Tipo de error: ${e.javaClass.simpleName}")
        
        val errorMessage = when {
            e.message?.contains("network") == true -> "Error de conexión. Verifica tu internet."
            e.message?.contains("cancelled") == true -> "Autenticación cancelada por el usuario."
            e.message?.contains("invalid") == true -> "Configuración de GitHub inválida."
            e.message?.contains("not enabled") == true -> "GitHub OAuth no está habilitado en Firebase Console. Ve a Firebase Console > Authentication > Sign-in method > GitHub y habilítalo."
            e.message?.contains("configuration") == true -> "Configuración de OAuth incorrecta."
            e.message?.contains("developer") == true -> "GitHub OAuth no está configurado. Ve a Firebase Console y habilita GitHub como proveedor de autenticación."
            else -> "Error en el login con GitHub: ${e.message}"
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
    
    private fun iniciarSesionFacebook() {
        Log.d("FacebookAuth", "Iniciando autenticación con Facebook SDK...")
        
        try {
            if (auth == null) {
                Log.e("FacebookAuth", "Firebase Auth no está inicializado")
                Toast.makeText(this, "Error: Firebase Auth no está configurado", Toast.LENGTH_LONG).show()
                return
            }
            
            // Mostrar mensaje de carga
            Toast.makeText(this, "Conectando con Facebook...", Toast.LENGTH_SHORT).show()
            
            // Configurar el callback de Facebook
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("FacebookAuth", "Facebook login exitoso")
                    handleFacebookAccessToken(loginResult.accessToken)
                }
                
                override fun onCancel() {
                    Log.d("FacebookAuth", "Facebook login cancelado")
                    Toast.makeText(this@LoginActivity, "Login con Facebook cancelado", Toast.LENGTH_SHORT).show()
                }
                
                override fun onError(exception: FacebookException) {
                    Log.e("FacebookAuth", "Error en Facebook login: ${exception.message}")
                    Toast.makeText(this@LoginActivity, "Error en Facebook login: ${exception.message}", Toast.LENGTH_LONG).show()
                }
            })
            
            // Iniciar el login de Facebook
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
            
        } catch (e: Exception) {
            Log.e("FacebookAuth", "Excepción al iniciar autenticación: ${e.message}")
            Log.e("FacebookAuth", "Stack trace: ${e.stackTraceToString()}")
            Toast.makeText(this, "Error al iniciar autenticación con Facebook: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("FacebookAuth", "handleFacebookAccessToken:$token")
        
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("FacebookAuth", "signInWithCredential:success")
                    val user = auth.currentUser
                    manejarLoginExitoso(user, "Facebook")
                } else {
                    Log.w("FacebookAuth", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Error de autenticación con Firebase: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
    
    private fun manejarLoginExitoso(user: com.google.firebase.auth.FirebaseUser?, provider: String) {
        Log.d("${provider}Auth", "Autenticado exitosamente como: ${user?.displayName}")
        Log.d("${provider}Auth", "Email: ${user?.email}")
        Log.d("${provider}Auth", "UID: ${user?.uid}")
        Toast.makeText(this, "Login con $provider exitoso", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun manejarErrorFacebook(e: Exception) {
        Log.e("FacebookAuth", "Error en autenticación: ${e.message}")
        Log.e("FacebookAuth", "Tipo de error: ${e.javaClass.simpleName}")
        Log.e("FacebookAuth", "Stack trace completo: ${e.stackTraceToString()}")
        
        val errorMessage = when {
            e.message?.contains("network") == true -> "Error de conexión. Verifica tu internet."
            e.message?.contains("cancelled") == true -> "Autenticación cancelada por el usuario."
            e.message?.contains("invalid") == true -> "Configuración de Facebook inválida."
            e.message?.contains("not enabled") == true -> "Facebook OAuth no está habilitado en Firebase Console.\n\nVe a Firebase Console > Authentication > Sign-in method > Facebook y habilítalo."
            e.message?.contains("configuration") == true -> "Configuración de OAuth incorrecta."
            e.message?.contains("developer") == true -> "Facebook OAuth no está configurado. Ve a Firebase Console y habilita Facebook como proveedor de autenticación."
            e.message?.contains("sign_in_failed") == true -> "Error de autenticación. Verifica que Facebook esté habilitado en Firebase Console."
            e.message?.contains("web_client_id") == true -> "Error de configuración. Ve a Firebase Console > Authentication > Sign-in method > Facebook y configura el Web Client ID."
            e.message?.contains("app_not_configured") == true -> "La app no está configurada para Facebook. Ve a Firebase Console y habilita Facebook OAuth."
            else -> "Error en el login con Facebook: ${e.message}\n\nVerifica que Facebook esté habilitado en Firebase Console."
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}