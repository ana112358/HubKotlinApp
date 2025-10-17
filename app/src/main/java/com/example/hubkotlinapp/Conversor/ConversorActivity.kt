// ConversorActivity.kt
package com.example.hubkotlinapp.Conversor

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.hubkotlinapp.R
import com.example.hubkotlinapp.common.Logger
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.Locale


enum class ConversionType(val displayName: String, val units: Array<String>) {
    LENGTH(
        "Comprimento",
        arrayOf(
            "Milímetro (mm)",
            "Centímetro (cm)",
            "Metro (m)",
            "Quilômetro (km)",
            "Polegada (in)",
            "Pé (ft)",
            "Jarda (yd)",
            "Milha (mi)"
        )
    ),
    AREA(
        "Área",
        arrayOf(
            "Metro² (m²)",
            "Centímetro² (cm²)",
            "Polegada² (in²)",
            "Pé² (ft²)",
            "Are (a)",
            "Acre (ac)",
            "Hectare (ha)"
        )
    ),
    DATA(
        "Dados",
        arrayOf(
            "Bit",
            "Byte",
            "Kilobyte (KB)",
            "Megabyte (MB)",
            "Gigabyte (GB)",
            "Terabyte (TB)"
        )
    ),
    TIP("Gorjeta", arrayOf())
}



class ConversorActivity : AppCompatActivity() {

    // TAG para o Logger
    private val TAG = "ConversorActivity"

    // Chave para salvar o estado
    companion object {
        private const val KEY_CURRENT_INPUT = "current_input"
    }

    private lateinit var toolbar: MaterialToolbar
    private lateinit var spinnerTipoConversao: Spinner
    private lateinit var layoutConversorPadrao: LinearLayout
    private lateinit var layoutConversorGorjeta: LinearLayout
    private lateinit var layoutTecladoNumerico: ConstraintLayout
    private lateinit var txtValorDe: TextView
    private lateinit var txtValorPara: TextView
    private lateinit var spinnerUnidadeDe: Spinner
    private lateinit var spinnerUnidadePara: Spinner
    private lateinit var editSubtotal: TextInputEditText
    private lateinit var editPorcentagemGorjeta: TextInputEditText
    private lateinit var editNumeroPessoas: TextInputEditText
    private lateinit var txtResultadoGorjeta: TextView
    private lateinit var iconSwap: ImageView

    private var currentInput = "0"
    private lateinit var selectedConversionType: ConversionType
    private var activeTipEditText: EditText? = null
    private var isRecreating = false // Flag para controlar a recriação

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversor)

        Logger.i(TAG, "Activity do Conversor iniciado. O Bundle savedInstanceState é ${if (savedInstanceState == null) "NULO" else "VÁLIDO"}.")

        bindViews()
        setupListeners()
        setupMainSpinner()

        if (savedInstanceState != null) {
            isRecreating = true // ATIVA a flag durante a recriação
            currentInput = savedInstanceState.getString(KEY_CURRENT_INPUT, "0")
            Logger.d(TAG, "Estado restaurado: a variável 'currentInput' agora é '$currentInput'")
        } else {
            // Se for a primeira inicialização, define um valor padrão para o spinner.
            spinnerTipoConversao.setSelection(0)
        }

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            iconSwap.setColorFilter(ContextCompat.getColor(this, R.color.white))
            toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        } else {
            iconSwap.setColorFilter(ContextCompat.getColor(this, R.color.black))
            toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.black))
        }
    }

    override fun onResume() {
        super.onResume()
        // Apenas desativamos a flag aqui, após a recriação ter sido concluída.
        if (isRecreating) {
            isRecreating = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Logger.i(TAG, "onSaveInstanceState chamado para salvar o estado.")
        outState.putString(KEY_CURRENT_INPUT, currentInput)
        Logger.d(TAG, "Estado salvo: a variável 'currentInput' ('$currentInput') foi guardada no Bundle.")
    }

    private fun bindViews() {
        toolbar = findViewById(R.id.toolbar)
        spinnerTipoConversao = findViewById(R.id.spinnerTipoConversao)
        layoutConversorPadrao = findViewById(R.id.layoutConversorPadrao)
        layoutConversorGorjeta = findViewById(R.id.layoutConversorGorjeta)
        layoutTecladoNumerico = findViewById(R.id.tecladoIncluido)
        txtValorDe = findViewById(R.id.txtValorDe)
        txtValorPara = findViewById(R.id.txtValorPara)
        spinnerUnidadeDe = findViewById(R.id.spinnerUnidadeDe)
        spinnerUnidadePara = findViewById(R.id.spinnerUnidadePara)
        editSubtotal = findViewById(R.id.editSubtotal)
        editPorcentagemGorjeta = findViewById(R.id.editPorcentagemGorjeta)
        editNumeroPessoas = findViewById(R.id.editNumeroPessoas)
        txtResultadoGorjeta = findViewById(R.id.txtResultadoGorjeta)
        iconSwap = findViewById(R.id.iconSwap)
    }

    private fun setupListeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val digitClickListener = View.OnClickListener { view ->
            if (view is Button) {
                onDigitClick(view)
            }
        }
        val ids = arrayOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot)
        ids.forEach { findViewById<Button>(it).setOnClickListener(digitClickListener) }

        findViewById<Button>(R.id.btnClear).setOnClickListener { onClearClick() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { onBackspaceClick() }
        findViewById<Button>(R.id.btnSwap).setOnClickListener { onSwapClick() }
        findViewById<ImageView>(R.id.iconSwap).setOnClickListener { onSwapClick() }

        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            if (selectedConversionType == ConversionType.TIP) {
                calculateTip()
            }
        }

        val unitSpinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convert()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerUnidadeDe.onItemSelectedListener = unitSpinnerListener
        spinnerUnidadePara.onItemSelectedListener = unitSpinnerListener

        val tipFieldClickListener = View.OnClickListener { view ->
            setActiveTipField(view as EditText)
        }
        editSubtotal.setOnClickListener(tipFieldClickListener)
        editPorcentagemGorjeta.setOnClickListener(tipFieldClickListener)
        editNumeroPessoas.setOnClickListener(tipFieldClickListener)
    }

    private fun setActiveTipField(field: EditText?) {
        editSubtotal.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        editPorcentagemGorjeta.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        editNumeroPessoas.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        activeTipEditText = field
        activeTipEditText?.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))
    }

    private fun setupMainSpinner() {
        val conversionTypes = ConversionType.values().map { it.displayName }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, conversionTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoConversao.adapter = adapter

        spinnerTipoConversao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedConversionType = ConversionType.values()[position]
                updateUIForConversionType()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateUIForConversionType() {
        // *** ANÁLISE DO MAU FUNCIONAMENTO ***
        // Se a tela está sendo recriada, registramos o erro de ciclo de vida.
        if (isRecreating) {
            Logger.e(TAG, "ANÁLISE DE MAL FUNCIONAMENTO (CICLO DE VIDA): O valor 'currentInput' ($currentInput) foi restaurado, mas a UI está sendo recriada. A chamada a 'onClearClick()' a seguir irá apagar este valor, e a recriação dos adaptadores dos Spinners de unidade perderá o estado de seleção, demonstrando o bug de perda de estado.")
        }

        onClearClick()

        val isTipCalculator = (selectedConversionType == ConversionType.TIP)

        findViewById<Button>(R.id.btnEquals).visibility = if (isTipCalculator) View.VISIBLE else View.INVISIBLE
        findViewById<Button>(R.id.btnSwap).visibility = if (isTipCalculator) View.INVISIBLE else View.VISIBLE
        iconSwap.visibility = if (isTipCalculator) View.GONE else View.VISIBLE

        layoutTecladoNumerico.visibility = View.VISIBLE
        layoutConversorGorjeta.visibility = if (isTipCalculator) View.VISIBLE else View.GONE
        layoutConversorPadrao.visibility = if (isTipCalculator) View.GONE else View.VISIBLE

        if (isTipCalculator) {
            setActiveTipField(editSubtotal)
        } else {
            setActiveTipField(null)
            val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, selectedConversionType.units)
            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUnidadeDe.adapter = unitAdapter
            spinnerUnidadePara.adapter = unitAdapter
        }
    }

    fun onDigitClick(view: View) {
        if (view !is Button) return
        val digit = view.text.toString()
        val isTipCalculator = (selectedConversionType == ConversionType.TIP)

        if (isTipCalculator) {
            activeTipEditText?.let { editText ->
                var currentText = editText.text.toString()
                if (currentText.isEmpty() && digit == ".") {
                    currentText = "0."
                } else if (digit == "." && currentText.contains(".")) {
                    return
                } else {
                    currentText += digit
                }
                editText.setText(currentText)
            }
        } else {
            if (currentInput == "0" && digit != ".") {
                currentInput = digit
            } else if (digit == "." && currentInput.contains(".")) {
                return
            } else if (currentInput.length < 15) {
                currentInput += digit
            }
            updateInputDisplay()
        }
    }

    private fun onClearClick() {
        if (!::selectedConversionType.isInitialized) return
        
        if (selectedConversionType == ConversionType.TIP) {
            editSubtotal.setText("")
            editPorcentagemGorjeta.setText("")
            editNumeroPessoas.setText("")
            txtResultadoGorjeta.text = ""
            setActiveTipField(editSubtotal)
        } else {
            currentInput = "0"
            updateInputDisplay()
        }
    }

    private fun onBackspaceClick() {
        val isTipCalculator = (selectedConversionType == ConversionType.TIP)
        if (isTipCalculator) {
            activeTipEditText?.let { editText ->
                val currentText = editText.text.toString()
                if (currentText.isNotEmpty()) {
                    editText.setText(currentText.dropLast(1))
                }
            }
        } else {
            if (currentInput.length > 1) {
                currentInput = currentInput.dropLast(1)
            } else {
                currentInput = "0"
            }
            updateInputDisplay()
        }
    }

    private fun onSwapClick() {
        if (selectedConversionType == ConversionType.TIP) return
        val fromIndex = spinnerUnidadeDe.selectedItemPosition
        val toIndex = spinnerUnidadePara.selectedItemPosition
        spinnerUnidadeDe.setSelection(toIndex)
        spinnerUnidadePara.setSelection(fromIndex)
    }

    private fun updateInputDisplay() {
        txtValorDe.text = currentInput
        convert()
    }

    private fun convert() {
        if (!::selectedConversionType.isInitialized || selectedConversionType == ConversionType.TIP) return
        if (spinnerUnidadeDe.selectedItem == null || spinnerUnidadePara.selectedItem == null) return

        val inputValue = currentInput.toDoubleOrNull() ?: 0.0
        val fromUnit = spinnerUnidadeDe.selectedItem.toString()
        val toUnit = spinnerUnidadePara.selectedItem.toString()

        val valueInBaseUnit = when (fromUnit) {
            "Metro (m)" -> inputValue
            "Milímetro (mm)" -> inputValue / 1000.0
            "Centímetro (cm)" -> inputValue / 100.0
            "Quilômetro (km)" -> inputValue * 1000.0
            "Polegada (in)" -> inputValue * 0.0254
            "Pé (ft)" -> inputValue * 0.3048
            "Jarda (yd)" -> inputValue * 0.9144
            "Milha (mi)" -> inputValue * 1609.34
            "Metro² (m²)" -> inputValue
            "Centímetro² (cm²)" -> inputValue / 10000.0
            "Polegada² (in²)" -> inputValue * 0.00064516
            "Pé² (ft²)" -> inputValue * 0.092903
            "Are (a)" -> inputValue * 100.0
            "Acre (ac)" -> inputValue * 4046.86
            "Hectare (ha)" -> inputValue * 10000.0
            "Byte" -> inputValue
            "Bit" -> inputValue / 8.0
            "Kilobyte (KB)" -> inputValue * 1024.0
            "Megabyte (MB)" -> inputValue * 1024.0 * 1024.0
            "Gigabyte (GB)" -> inputValue * 1024.0 * 1024.0 * 1024.0
            "Terabyte (TB)" -> inputValue * 1024.0 * 1024.0 * 1024.0 * 1024.0
            else -> inputValue
        }

        val outputValue = when (toUnit) {
            "Metro (m)" -> valueInBaseUnit
            "Milímetro (mm)" -> valueInBaseUnit * 1000.0
            "Centímetro (cm)" -> valueInBaseUnit * 100.0
            "Quilômetro (km)" -> valueInBaseUnit / 1000.0
            "Polegada (in)" -> valueInBaseUnit / 0.0254
            "Pé (ft)" -> valueInBaseUnit / 0.3048
            "Jarda (yd)" -> valueInBaseUnit / 0.9144
            "Milha (mi)" -> valueInBaseUnit / 1609.34
            "Metro² (m²)" -> valueInBaseUnit
            "Centímetro² (cm²)" -> valueInBaseUnit * 10000.0
            "Polegada² (in²)" -> valueInBaseUnit / 0.00064516
            "Pé² (ft²)" -> valueInBaseUnit / 0.092903
            "Are (a)" -> valueInBaseUnit / 100.0
            "Acre (ac)" -> valueInBaseUnit / 4046.86
            "Hectare (ha)" -> valueInBaseUnit / 10000.0
            "Byte" -> valueInBaseUnit
            "Bit" -> valueInBaseUnit * 8.0
            "Kilobyte (KB)" -> valueInBaseUnit / 1024.0
            "Megabyte (MB)" -> valueInBaseUnit / (1024.0 * 1024.0)
            "Gigabyte (GB)" -> valueInBaseUnit / (1024.0 * 1024.0 * 1024.0)
            "Terabyte (TB)" -> valueInBaseUnit / (1024.0 * 1024.0 * 1024.0 * 1024.0)
            else -> valueInBaseUnit
        }

        txtValorPara.text = formatResult(outputValue)
    }

    private fun formatResult(value: Double): String {
        val formattedResult = if (value < 0.0001 && value > 0) {
            String.format(Locale.US, "%.8f", value)
        } else {
            String.format(Locale.US, "%.4f", value)
        }
        return formattedResult.trimEnd('0').trimEnd('.')
    }

    private fun calculateTip() {
        val subtotal = editSubtotal.text.toString().toDoubleOrNull() ?: 0.0
        val percentage = editPorcentagemGorjeta.text.toString().toDoubleOrNull() ?: 15.0
        val people = editNumeroPessoas.text.toString().toIntOrNull() ?: 1

        if (people < 1) {
            txtResultadoGorjeta.text = "Divisão por zero!"
            return
        }

        val tipAmount = subtotal * (percentage / 100)
        val totalAmount = subtotal + tipAmount
        val amountPerPerson = totalAmount / people

        val currencyFormat = NumberFormat.getCurrencyInstance()
        txtResultadoGorjeta.text = """
            Gorjeta: ${currencyFormat.format(tipAmount)}
            Total: ${currencyFormat.format(totalAmount)}
            Por Pessoa: ${currencyFormat.format(amountPerPerson)}
        """.trimIndent()
    }
}
