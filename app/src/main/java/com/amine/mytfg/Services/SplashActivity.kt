package com.amine.mytfg

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Espera 2 segundos antes de iniciar la actividad principal
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, activityPrincipal::class.java)
            startActivity(intent)
            finish()
        }, 2000) // 2000 ms = 2 segundos
    }
}
