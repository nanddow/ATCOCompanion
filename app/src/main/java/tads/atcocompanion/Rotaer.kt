package tads.atcocompanion

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Rotaer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notam)

        val btbuscarotaer = findViewById<Button>(R.id.btbuscanotam)
        val editIcaoCode = findViewById<EditText>(R.id.editIcaoCode)
        val txtResultado = findViewById<TextView>(R.id.txtResultado)

        btbuscarotaer.setOnClickListener {
            val icaoCode = editIcaoCode.text.toString().uppercase()
            if (icaoCode.isNotBlank()) {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                txtResultado.text = "Buscando informações do ROTAER..."
                buscarRotaer(icaoCode, txtResultado)
            } else {
                txtResultado.text = "Por favor, insira um código ICAO válido."
            }
        }
    }

    private fun buscarRotaer(icaoCode: String, txtResultado: TextView) {

    }
}
