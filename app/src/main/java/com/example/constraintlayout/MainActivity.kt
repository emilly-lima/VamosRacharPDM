package com.example.constraintlayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity() , TextWatcher, TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var edtConta: EditText
    private lateinit var edtPessoas: EditText
    private lateinit var resultado: TextView
    private var ttsSucess: Boolean = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edtConta = findViewById<EditText>(R.id.edtConta)
        edtConta.addTextChangedListener(this)

        edtPessoas = findViewById<EditText>(R.id.edtPessoas)
        edtPessoas.addTextChangedListener(this)

        resultado = findViewById<TextView>(R.id.resultado)
        // Initialize TTS engine
        tts = TextToSpeech(this, this)

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        Log.d("PDM24","Antes de mudar")

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        Log.d("PDM24","Mudando")
    }

    override fun afterTextChanged(s: Editable?) {
        val strConta = edtConta.text.toString()
        val strPessoa = edtPessoas.text.toString()

        if (strConta.isNotEmpty() && strPessoa.isNotEmpty()) {
            try {
                val conta = strConta.toDouble()
                val pessoas = strPessoa.toInt()

                if (pessoas > 1) {
                    val resultadoNum = (conta / pessoas).toDouble()
                    resultado.text = "R$ %.2f".format(resultadoNum)
                }
                if (conta == 0.toDouble()){
                    resultado.text = "Insira o valor!"
                }
                else {
                    resultado.text = "Deve ter ao menos duas pessoas."
                }
            } catch (e: NumberFormatException) {
                resultado.text = "Valor inválido, digite um número."
            }
        } else {
            resultado.text = ""
        }
    }

    fun clickFalar(v: View){
        if (tts.isSpeaking) {
            tts.stop()
        }
        if(ttsSucess) {
            Log.d ("PDM23", tts.language.toString())
            tts.speak("Total a pagar: " + resultado.text.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun clickCompartilhar(v: View){
        val strConta = edtConta.text.toString()
        val strPessoa = edtPessoas.text.toString()
        val textoResultado = resultado.text.toString()

        try {
            val conta = strConta.toDouble()
            val pessoas = strPessoa.toInt()

            if (conta == 0.0 || pessoas == 0) {
                return
            }

            if (textoResultado.isBlank() || textoResultado.contains("Deve ter ao menos") || textoResultado.contains("Valor inválido")) {
                return
            }

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Faz o pix! O valor ficou $textoResultado pra cada um.")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

        } catch (e: NumberFormatException) {
            return
        }
    }

    override fun onDestroy() {
        // Release TTS engine resources
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // TTS engine is initialized successfully
            tts.language = Locale.getDefault()
            ttsSucess=true
            Log.d("PDM23","Sucesso na Inicialização")
        } else {
            // TTS engine failed to initialize
            Log.e("PDM23", "Failed to initialize TTS engine.")
            ttsSucess=false
        }
    }
}

