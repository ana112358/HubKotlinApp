package com.example.hubkotlinapp

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.hubkotlinapp.basquete.BasqueteActivity
import com.example.hubkotlinapp.calculadora.CalculadoraActivity
import com.example.hubkotlinapp.Conversor.ConversorActivity
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardBasquete = findViewById<ImageView>(R.id.basquete_card)
        val cardCalculadora = findViewById<ImageView>(R.id.calculadora_card)
        val cardConversor = findViewById<ImageView>(R.id.conversor_card)

        cardBasquete.setOnClickListener {
            // Inicia a atividade de Basquete
            val intent = Intent(this, BasqueteActivity::class.java) // Adapte o nome da sua Activity
            startActivity(intent)
        }

        cardCalculadora.setOnClickListener {
            // Inicia a atividade da Calculadora
            val intent = Intent(this, CalculadoraActivity::class.java) // Adapte o nome da sua Activity
            startActivity(intent)
        }

        cardConversor.setOnClickListener {
            // Inicia a atividade do Conversor
            val intent = Intent(this, ConversorActivity::class.java) // Adapte o nome da sua Activity
            startActivity(intent)
        }
    }
}
