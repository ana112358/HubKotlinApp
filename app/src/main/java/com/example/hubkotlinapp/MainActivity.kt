package com.example.hubkotlinapp

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import com.example.hubkotlinapp.basquete.BasqueteActivity
import com.example.hubkotlinapp.calculadora.CalculadoraActivity
import com.example.hubkotlinapp.Conversor.ConversorActivity
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val themeSwitch = findViewById<ImageView>(R.id.themeSwitch)
        val cardBasquete = findViewById<ImageView>(R.id.basquete_card)
        val cardCalculadora = findViewById<ImageView>(R.id.calculadora_card)
        val cardConversor = findViewById<ImageView>(R.id.conversor_card)

        val isDarkMode = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

        themeSwitch.setImageResource(R.drawable.hub_modo)

        themeSwitch.setOnClickListener {
            val currentMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            val newMode = if (currentMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }

            AppCompatDelegate.setDefaultNightMode(newMode)
            recreate()
        }

        cardBasquete.setOnClickListener {
            val intent = Intent(this, BasqueteActivity::class.java)
            startActivity(intent)
        }

        cardCalculadora.setOnClickListener {
            val intent = Intent(this, CalculadoraActivity::class.java)
            startActivity(intent)
        }

        cardConversor.setOnClickListener {
            val intent = Intent(this, ConversorActivity::class.java)
            startActivity(intent)
        }
    }
}
