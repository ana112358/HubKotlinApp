package com.example.hubkotlinapp.calculadora

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.example.hubkotlinapp.R

class CalculadoraActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)

        val buttonVoltar = findViewById<Button>(R.id.buttonVoltar)
        buttonVoltar.setOnClickListener {
            finish()
        }
    }
}
