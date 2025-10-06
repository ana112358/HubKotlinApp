package com.example.hubkotlinapp.basquete

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.LinearLayout
import android.widget.ScrollView
import com.example.hubkotlinapp.R

class BasqueteActivity : Activity() {

    private var pontuacaoTimeA: Int = 0
    private var pontuacaoTimeB: Int = 0
    private var faltasTimeA: Int = 0
    private var faltasTimeB: Int = 0

    private lateinit var pTimeA: TextView
    private lateinit var pTimeB: TextView
    private lateinit var faltasA: TextView
    private lateinit var faltasB: TextView
    private lateinit var containerHistorico: LinearLayout
    private lateinit var scrollHistorico: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basquete)

        pTimeA = findViewById(R.id.placarTimeA)
        pTimeB = findViewById(R.id.placarTimeB)
        faltasA = findViewById(R.id.faltasTimeA)
        faltasB = findViewById(R.id.faltasTimeB)
        containerHistorico = findViewById(R.id.containerHistorico)
        scrollHistorico = findViewById(R.id.scrollHistorico)

        // Botões Time A
        findViewById<Button>(R.id.tresPontosA).setOnClickListener { adicionarPontos(3, "A") }
        findViewById<Button>(R.id.doisPontosA).setOnClickListener { adicionarPontos(2, "A") }
        findViewById<Button>(R.id.tiroLivreA).setOnClickListener { adicionarPontos(1, "A") }
        findViewById<Button>(R.id.faltaTimeA).setOnClickListener { adicionarFalta("A") }

        // Botões Time B
        findViewById<Button>(R.id.tresPontosB).setOnClickListener { adicionarPontos(3, "B") }
        findViewById<Button>(R.id.doisPontosB).setOnClickListener { adicionarPontos(2, "B") }
        findViewById<Button>(R.id.tiroLivreB).setOnClickListener { adicionarPontos(1, "B") }
        findViewById<Button>(R.id.faltaTimeB).setOnClickListener { adicionarFalta("B") }

        // Botão reiniciar
        findViewById<Button>(R.id.reiniciarPartida).setOnClickListener { reiniciarPartida() }

        // Botão voltar
        findViewById<Button>(R.id.buttonVoltar).setOnClickListener { finish() }

        atualizarTodosDisplays()
    }


    private fun adicionarPontos(pontos: Int, time: String) {
        if (time == "A") {
            pontuacaoTimeA += pontos
            adicionarEvento("🏀 Time A marcou $pontos ponto(s)")
        } else {
            pontuacaoTimeB += pontos
            adicionarEvento("🏀 Time B marcou $pontos ponto(s)")
        }
        atualizarTodosDisplays()
    }

    private fun adicionarFalta(time: String) {
        if (time == "A") {
            faltasTimeA++
            adicionarEvento("⚠️ Falta cometida pelo Time A")
            if (faltasTimeA >= 5) Toast.makeText(this, "⚠️ Time A: Limite de faltas atingido!", Toast.LENGTH_LONG).show()
        } else {
            faltasTimeB++
            adicionarEvento("⚠️ Falta cometida pelo Time B")
            if (faltasTimeB >= 5) Toast.makeText(this, "⚠️ Time B: Limite de faltas atingido!", Toast.LENGTH_LONG).show()
        }
        atualizarTodosDisplays()
    }

    private fun atualizarTodosDisplays() {
        pTimeA.text = pontuacaoTimeA.toString()
        pTimeB.text = pontuacaoTimeB.toString()
        faltasA.text = "Faltas: $faltasTimeA"
        faltasB.text = "Faltas: $faltasTimeB"

        if (pontuacaoTimeA > pontuacaoTimeB) {
            pTimeA.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            pTimeB.setTextColor(resources.getColor(android.R.color.white))
        } else if (pontuacaoTimeB > pontuacaoTimeA) {
            pTimeB.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            pTimeA.setTextColor(resources.getColor(android.R.color.white))
        } else {
            pTimeA.setTextColor(resources.getColor(android.R.color.white))
            pTimeB.setTextColor(resources.getColor(android.R.color.white))
        }
    }

    private fun reiniciarPartida() {
        pontuacaoTimeA = 0
        pontuacaoTimeB = 0
        faltasTimeA = 0
        faltasTimeB = 0

        containerHistorico.removeAllViews()
        adicionarEvento("🔄 Partida reiniciada")
        atualizarTodosDisplays()
        Toast.makeText(this, "Placar reiniciado!", Toast.LENGTH_SHORT).show()
    }

    private fun adicionarEvento(texto: String) {
        val novoEvento = TextView(this)
        novoEvento.text = texto
        novoEvento.setTextColor(resources.getColor(android.R.color.white))
        novoEvento.textSize = 14f
        novoEvento.setPadding(4, 4, 4, 4)
        containerHistorico.addView(novoEvento)

        scrollHistorico.post { scrollHistorico.fullScroll(ScrollView.FOCUS_DOWN) }
    }
}
