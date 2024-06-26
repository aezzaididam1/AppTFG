package com.amine.mytfg.InicioSesion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amine.mytfg.R
import com.amine.mytfg.SplashActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider

class InicioSesionActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_sesion_view)
        val textViewLogin = findViewById<TextView>(R.id.textViewLogin)
        textViewLogin.isClickable = false
        auth = FirebaseAuth.getInstance()

        // Configuración de Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("532281414685-glev98rfdeld4a87dp8njsmj018j13kc.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    fun IniciarSesion(view: View) {
        val email = findViewById<EditText>(R.id.edt_email).text.toString().trim()
        val password = findViewById<EditText>(R.id.edt_contraseña).text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "Los campos de correo electrónico y contraseña no pueden estar vacíos.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        signIn(email, password)
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("SignIn", "signInWithEmail:success")
                    startActivity(Intent(this, SplashActivity::class.java))
                    finish()
                } else {
                    task.exception?.let {
                        val message = when (it) {
                            is FirebaseAuthException -> when (it.errorCode) {
                                "ERROR_INVALID_EMAIL" -> "Formato de correo electrónico inválido."
                                "ERROR_WRONG_PASSWORD" -> "Contraseña incorrecta."
                                "ERROR_USER_NOT_FOUND" -> "Usuario no encontrado."
                                else -> "Error de autenticación."
                            }
                            else -> "Error de autenticación."
                        }
                        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    // Método para el inicio de sesión con Google
    fun signInWithGoogle(view: View) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Método para manejar el resultado del inicio de sesión con Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "El inicio de sesión con Google ha fallado", e)
            }
        }
    }

    // Método para autenticar con Firebase usando el token de Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("FirebaseAuth", "Inicio de sesión con Google: éxito")
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            } else {
                Log.w("FirebaseAuth", "Autenticación fallida", task.exception)
                Toast.makeText(this, "Autenticación fallida.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun RegistroActivity(view: View) {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }
}
