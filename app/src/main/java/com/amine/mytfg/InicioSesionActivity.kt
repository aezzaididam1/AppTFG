package com.amine.mytfg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.auth

class InicioSesionActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_sesion_view)
        val textViewLogin = findViewById<TextView>(R.id.textViewLogin)
        textViewLogin.isClickable = false
        auth = Firebase.auth


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
                    startActivity(Intent(this, checkCampos::class.java))
                    finish()
                } else {
                    // Manera simplificada de manejar el error
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


    fun RegistroActivity(view: View) {

        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)

    }


}