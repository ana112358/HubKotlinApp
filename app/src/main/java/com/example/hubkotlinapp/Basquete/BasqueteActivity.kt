package com.example.hubkotlinapp.basquete

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.example.hubkotlinapp.R

class BasqueteActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basquete)

        val buttonVoltar = findViewById<Button>(R.id.buttonVoltar)
        buttonVoltar.setOnClickListener {
            finish() // volta para o Hub
        }
    }
}

