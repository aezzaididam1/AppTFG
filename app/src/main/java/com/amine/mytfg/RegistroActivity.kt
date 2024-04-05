package com.amine.mytfg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegistroActivity : AppCompatActivity() {

    // Declarar la instancia de FirebaseAuth
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        val textViewRegister = findViewById<TextView>(R.id.textViewRegister)
        textViewRegister.isClickable = false

        // Inicializar la instancia de FirebaseAuth
        auth = FirebaseAuth.getInstance()

    }

    fun botonCrearCuenta(view: View) {

        // 1. Obtener los valores ingresados en los campos de correo y contraseña
        val emailTextView = findViewById<TextView>(R.id.edt_email_registro)
        val contraseñaTextView = findViewById<TextView>(R.id.edt_contraseña_registro1)
        val email = emailTextView.text.toString()
        val contraseña = contraseñaTextView.text.toString()
        val nombreTextView = findViewById<TextView>(R.id.edt_nombre)
        val nombre = nombreTextView.text.toString()


        // 2. Validar los campos
        if (email.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Validar el formato de correo electrónico
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Formato de correo electrónico incorrecto", Toast.LENGTH_SHORT).show()
            return
        }

        // 4. Validar la longitud y composición de la contraseña
        if (!validarContraseña(contraseña)) {
            Toast.makeText(
                this,
                "La contraseña debe tener al menos 8 caracteres, 1 minúscula, 1 mayúscula y 1 número",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, contraseña)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso, puedes dirigir al usuario a la pantalla principal
                    // o realizar otras acciones necesarias, como guardar información adicional en Firestore/Realtime Database
                    Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()

                    // Opcional: Redirigir al usuario a otra Activity tras el registro exitoso
                    val intent = Intent(this, checkCampos::class.java)
                    startActivity(intent)
                    finish() // Finaliza la actividad actual
                } else {
                    // Si el registro falla, muestra un mensaje al usuario
                    Toast.makeText(this, "Registro fallido: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun validarContraseña(contraseña: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}\$")
        return regex.matches(contraseña)
    }
    fun InicioSesionActivity(view: View) {
        val intent = Intent(this, InicioSesionActivity::class.java)
        startActivity(intent)
    }
}