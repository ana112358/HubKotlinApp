package com.example.hubkotlinapp.calculadora

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.hubkotlinapp.R // Import correto do R do Hub
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlin.math.*

data class HistoricoItem(
    val operacao: String,
    val resultado: String
)

class CalculadoraActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private val historicoLista = mutableListOf<HistoricoItem>()
    private var currentInput: String = ""
    private var operand: Double? = null
    private var pendingOp: String? = null
    private var isRadianMode: Boolean = true

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora)


        val btnToggle = findViewById<ImageButton>(R.id.btnToggleTheme)
        updateToggleIcon(btnToggle)

        btnToggle.setOnClickListener {
            val isDayMode = (resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES

            if (isDayMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // 2. Se for modo escuro, pinta a seta de branco
            toolbar.navigationIcon?.setTint(getColor(R.color.white))
        } else {
            // 3. Se for modo claro, pinta a seta de preto
            toolbar.navigationIcon?.setTint(getColor(R.color.black))
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
        // --- O restante do seu código onCreate permanece o mesmo ---
        tvDisplay = findViewById(R.id.txtResultado)

        val digits = listOf(
            "0" to R.id.btn0, "1" to R.id.btn1, "2" to R.id.btn2, "3" to R.id.btn3,
            "4" to R.id.btn4, "5" to R.id.btn5, "6" to R.id.btn6, "7" to R.id.btn7,
            "8" to R.id.btn8, "9" to R.id.btn9, "." to R.id.btnPonto
        )
        digits.forEach { (digit, id) ->
            findViewById<Button>(id)?.setOnClickListener { appendDigit(digit) }
        }

        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            val gridButtonsRetrato = findViewById<GridLayout>(R.id.gridLeft)
            val iconeHistoricoRetrato = findViewById<ImageView>(R.id.iconHistoric)
            val historicoBoxRetrato = findViewById<LinearLayout>(R.id.historicoBox)
            val btnLimparHistoricoRetrato = findViewById<Button>(R.id.btnLimparHistorico)

            if (gridButtonsRetrato != null && iconeHistoricoRetrato != null && historicoBoxRetrato != null && btnLimparHistoricoRetrato != null) {
                var isButtonsVisible = true
                gridButtonsRetrato.visibility = View.VISIBLE
                historicoBoxRetrato.visibility = View.GONE

                iconeHistoricoRetrato.setOnClickListener {
                    isButtonsVisible = !isButtonsVisible
                    if (isButtonsVisible) {
                        gridButtonsRetrato.animate().alpha(1f).setDuration(200).start()
                        historicoBoxRetrato.animate().alpha(0f).setDuration(200).start()
                        gridButtonsRetrato.visibility = View.VISIBLE
                        historicoBoxRetrato.visibility = View.GONE
                    } else {
                        gridButtonsRetrato.animate().alpha(0f).setDuration(200).start()
                        historicoBoxRetrato.animate().alpha(1f).setDuration(200).start()
                        gridButtonsRetrato.visibility = View.GONE
                        historicoBoxRetrato.visibility = View.VISIBLE
                    }
                }

                btnLimparHistoricoRetrato.setOnClickListener {
                    historicoLista.clear()
                    val historicoConteudo = findViewById<LinearLayout>(R.id.historicoConteudo)
                    historicoConteudo?.removeAllViews()
                }
            }
        }

        val ops = listOf(
            "+" to R.id.btnSomar, "-" to R.id.btnSubtrair,
            "×" to R.id.btnMultiplicar, "÷" to R.id.btnDividir
        )
        ops.forEach { (op, id) ->
            findViewById<Button>(id)?.setOnClickListener { onOperator(op) }
        }

        findViewById<Button>(R.id.btnIgual)?.setOnClickListener { onEquals() }
        findViewById<Button>(R.id.btnClear)?.setOnClickListener { clearAll() }
        findViewById<MaterialButton>(R.id.btnBackspace)?.setOnClickListener { backspace() }
        findViewById<Button>(R.id.btnPorcentagem)?.setOnClickListener { onPercentage() }
        findViewById<Button>(R.id.btnSin)?.setOnClickListener { onTrigFunction("sin") }
        findViewById<Button>(R.id.btnCos)?.setOnClickListener { onTrigFunction("cos") }
        findViewById<Button>(R.id.btnTan)?.setOnClickListener { onTrigFunction("tan") }
        findViewById<Button>(R.id.btnLog)?.setOnClickListener { onLogFunction("log") }
        findViewById<Button>(R.id.btnLn)?.setOnClickListener { onLogFunction("ln") }
        findViewById<Button>(R.id.btnPow)?.setOnClickListener { onPowerFunction("x²") }
        findViewById<Button>(R.id.btnPowerXY)?.setOnClickListener { onOperator("^") }
        findViewById<Button>(R.id.btnSqrt)?.setOnClickListener { onUnaryOperation("sqrt") }
        findViewById<Button>(R.id.btnRad)?.setOnClickListener { toggleAngleMode() }

        findViewById<Button>(R.id.btnChange)?.setOnClickListener {
            requestedOrientation = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

        updateDisplay()
    }

    // ### INÍCIO DA CORREÇÃO ###
    // O tipo do parâmetro 'item' foi corrigido para usar a classe HistoricoItem local.
    private fun adicionarHistoricoNaTelaParcial(item: HistoricoItem) {
        val historicoConteudo = findViewById<LinearLayout>(R.id.historicoConteudo)
        if (historicoConteudo == null) return

        val tvOperacao = TextView(this).apply {
            text = item.operacao
            // Use o contexto para obter a cor de forma segura
            setTextColor(context.getColor(R.color.typed_text))
            textSize = 18f
            gravity = View.TEXT_ALIGNMENT_TEXT_END
            setPadding(0, 0, 0, 4)
            setTextAppearance(R.style.HistoricoOperacao)
        }

        historicoConteudo.addView(tvOperacao)
    }

    // O tipo do parâmetro 'item' foi corrigido para usar a classe HistoricoItem local.
    private fun adicionarHistoricoNaTela(item: HistoricoItem) {
        val historicoConteudo = findViewById<LinearLayout>(R.id.historicoConteudo)
        if (historicoConteudo == null) return

        val tvOperacao = TextView(this).apply {
            text = item.operacao
            setTextColor(context.getColor(R.color.typed_text))
            textSize = 18f
            gravity = Gravity.END
            setPadding(0, 0, 0, 4)
            setTextAppearance(R.style.HistoricoOperacao)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END
            }
        }

        val tvResultado = TextView(this).apply {
            text = item.resultado
            setTextColor(context.getColor(R.color.result_text))
            textSize = 20f
            gravity = Gravity.END
            setPadding(0, 0, 0, 8)
            setTextAppearance(R.style.HistoricoResultado)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END
            }
        }

        historicoConteudo.addView(tvOperacao)
        historicoConteudo.addView(tvResultado)
    }
    // ### FIM DA CORREÇÃO ###

    // --- NENHUMA OUTRA MUDANÇA É NECESSÁRIA NO RESTANTE DA LÓGICA ---
    // (O resto do seu código permanece igual)
    private fun updateToggleIcon(btn: ImageButton) {
        val isDayMode = (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES

        if (isDayMode) {
            btn.setImageResource(R.drawable.daymode)
        } else {
            btn.setImageResource(R.drawable.darkmode)
        }
    }

    private fun appendDigit(d: String) {
        if (d == "." && currentInput.contains(".")) return
        currentInput = if (currentInput == "0" && d != ".") d else currentInput + d
        updateDisplay()
    }

    private fun onOperator(op: String) {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                if (operand == null) operand = value
                else operand = performOperation(operand!!, value, pendingOp)

                // Operação parcial no histórico
                val operacaoStr = "${formatResult(operand!!)} $op"
                val resultadoStr = ""
                // No modo retrato, atualiza a UI do histórico
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    val item = HistoricoItem(operacaoStr, resultadoStr)
                    historicoLista.add(item)
                    // adicionarHistoricoNaTelaParcial(item) // Pode ser confuso para o usuário, opcional
                }
            }
            currentInput = ""
        }
        pendingOp = op
        updateDisplay()
    }

    private fun onEquals() {
        if (operand != null && currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull() ?: return
            val result = performOperation(operand!!, value, pendingOp)

            val operacaoStr = "${formatResult(operand!!)} ${pendingOp ?: ""} ${formatResult(value)}"
            val resultadoStr = "= ${formatResult(result)}"

            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                val item = HistoricoItem(operacaoStr, resultadoStr)
                historicoLista.add(item)
                adicionarHistoricoNaTela(item)
            }

            operand = null
            pendingOp = null
            currentInput = formatResult(result)
            updateDisplay()
        }
    }

    private fun onPercentage() {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                val result = if (operand != null && pendingOp in listOf("+", "-")) {
                    operand!! * (value / 100)
                } else {
                    value / 100
                }
                currentInput = formatResult(result)
                updateDisplay()
            }
        }
    }

    private fun performOperation(a: Double, b: Double, op: String?): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) {
                Toast.makeText(this, "Divisão por zero", Toast.LENGTH_SHORT).show()
                a
            } else a / b
            "^" -> a.pow(b)
            else -> b
        }
    }

    private fun onTrigFunction(func: String) {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                try {
                    val angleInRadians = if (isRadianMode) value else Math.toRadians(value)
                    val result = when (func) {
                        "sin" -> sin(angleInRadians)
                        "cos" -> cos(angleInRadians)
                        "tan" -> {
                            val tanResult = tan(angleInRadians)
                            if (tanResult.isInfinite() || abs(tanResult) > 1e15) {
                                showError("Indefinido")
                                return
                            }
                            tanResult
                        }
                        else -> value
                    }
                    currentInput = formatResult(result)
                    updateDisplay()
                } catch (e: Exception) {
                    showError("Erro matemático")
                }
            }
        }
    }

    private fun onLogFunction(func: String) {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                if (value <= 0) {
                    showError("Log de número ≤ 0")
                    return
                }
                try {
                    val result = when (func) {
                        "log" -> log10(value)
                        "ln" -> ln(value)
                        else -> value
                    }
                    currentInput = formatResult(result)
                    updateDisplay()
                } catch (e: Exception) {
                    showError("Erro matemático")
                }
            }
        }
    }

    private fun onPowerFunction(func: String) {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                try {
                    val result = when (func) {
                        "x²" -> value.pow(2.0)
                        else -> value
                    }
                    currentInput = formatResult(result)
                    updateDisplay()
                } catch (e: Exception) {
                    showError("Erro matemático")
                }
            }
        }
    }

    private fun onUnaryOperation(operation: String) {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDoubleOrNull()
            if (value != null) {
                try {
                    val result = when (operation) {
                        "sqrt" -> {
                            if (value < 0) {
                                showError("Raiz de nº negativo")
                                return
                            }
                            sqrt(value)
                        }
                        else -> value
                    }
                    currentInput = formatResult(result)
                    updateDisplay()
                } catch (e: Exception) {
                    showError("Erro matemático")
                }
            }
        }
    }

    private fun toggleAngleMode() {
        isRadianMode = !isRadianMode
        val mode = if (isRadianMode) "RAD" else "DEG"
        Toast.makeText(this, "Modo: $mode", Toast.LENGTH_SHORT).show()
        findViewById<Button>(R.id.btnRad)?.text = mode
    }


    private fun formatResult(result: Double): String {
        return when {
            result.isInfinite() -> "∞"
            result.isNaN() -> "Erro"
            // Use uma comparação mais robusta para zero
            abs(result) < 1e-9 -> "0"
            result == result.toLong().toDouble() -> result.toLong().toString()
            else -> {
                String.format("%.6f", result).trimEnd('0').trimEnd('.')
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun clearAll() {
        currentInput = ""
        operand = null
        pendingOp = null
        updateDisplay()
    }

    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            if (currentInput.isEmpty()) {
                currentInput = "0"
            }
        } else if (pendingOp != null) {
            pendingOp = null
        } else if (operand != null) {
            currentInput = formatResult(operand!!)
            operand = null
        }
        updateDisplay()
    }


    private fun updateDisplay() {
        val displayText = if (currentInput.isNotEmpty()) {
            currentInput
        } else if (pendingOp != null && operand != null) {
            formatResult(operand!!) // Não mostra o operador pendente no display principal
        } else if (operand != null) {
            formatResult(operand!!)
        } else {
            "0"
        }
        tvDisplay.text = displayText
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentInput", currentInput)
        outState.putDouble("operand", operand ?: Double.NaN)
        outState.putString("pendingOp", pendingOp)
        outState.putBoolean("isRadianMode", isRadianMode)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentInput = savedInstanceState.getString("currentInput", "")
        val opnd = savedInstanceState.getDouble("operand", Double.NaN)
        operand = if (opnd.isNaN()) null else opnd
        pendingOp = savedInstanceState.getString("pendingOp")
        isRadianMode = savedInstanceState.getBoolean("isRadianMode", true)
        updateDisplay()
    }
}
