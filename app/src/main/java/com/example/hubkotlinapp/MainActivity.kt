package com.example.hubkotlinapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.hubkotlinapp.basquete.BasqueteActivity
import com.example.hubkotlinapp.calculadora.CalculadoraActivity
import com.example.hubkotlinapp.R

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonBasquete = findViewById<Button>(R.id.buttonBasquete)
        val buttonCalculadora = findViewById<Button>(R.id.buttonCalculadora)

        buttonBasquete.setOnClickListener {
            startActivity(Intent(this, BasqueteActivity::class.java))
        }

        buttonCalculadora.setOnClickListener {
            startActivity(Intent(this, CalculadoraActivity::class.java))
        }
    }
}
