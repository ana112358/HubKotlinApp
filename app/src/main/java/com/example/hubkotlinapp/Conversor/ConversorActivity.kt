package com.example.hubkotlinapp.Conversor

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.example.hubkotlinapp.R

class ConversorActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversor)

        val buttonVoltar = findViewById<Button>(R.id.buttonVoltarConversor)
        buttonVoltar.setOnClickListener {
            finish() // fecha a Activity e volta para a anterior
        }
    }
}
