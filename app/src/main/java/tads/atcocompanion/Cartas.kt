package tads.atcocompanion

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Cartas : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cartas)

        val btbuscacartas = findViewById<Button>(R.id.btbuscacartas)
        val editIcaoCode = findViewById<EditText>(R.id.editIcaoCode)
        val txtResultado = findViewById<TextView>(R.id.txtResultado)

        btbuscacartas.setOnClickListener {
            val icaoCode = editIcaoCode.text.toString().uppercase()
            if (icaoCode.isNotBlank()) {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                txtResultado.text = "Buscando Cartas..."

            } else {
                txtResultado.text = "Por favor, insira um código ICAO válido."
            }
        }
    }


}
