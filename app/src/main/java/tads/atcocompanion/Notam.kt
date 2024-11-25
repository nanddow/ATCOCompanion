package tads.atcocompanion

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class Notam : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notam)

        val btbuscanotam = findViewById<Button>(R.id.btbuscanotam)
        val editIcaoCode = findViewById<EditText>(R.id.editIcaoCode)
        val txtResultado = findViewById<TextView>(R.id.txtResultado)

        btbuscanotam.setOnClickListener {
            val icaoCode = editIcaoCode.text.toString().uppercase()
            if (icaoCode.isNotBlank()) {
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                txtResultado.text = "Buscando NOTAM..."
                buscarNotam(icaoCode, txtResultado)
            } else {
                txtResultado.text = "Por favor, insira um código ICAO válido."
            }
        }
    }

    private fun buscarNotam(icaoCode: String, txtResultado: TextView) {
        val client = OkHttpClient()
        val url = "https://applications.icao.int/dataservices/api/notams-realtime-list" +
                "?api_key=e32a2784-156e-4ceb-bd77-85117e071987" +
                "&format=json" +
                "&locations=$icaoCode"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    txtResultado.text = "Erro ao buscar NOTAM: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            txtResultado.text = "Erro na resposta: ${response.code}"
                        }
                    } else {
                        val responseData = response.body?.string()

                        if (responseData != null) {
                            try {
                                val notams = JSONArray(responseData)

                                if (notams.length() > 0) {
                                    val resultado = StringBuilder()

                                    for (i in 0 until notams.length()) {
                                        val notam = notams.getJSONObject(i)
                                        val id = notam.optString("id", "Sem ID")
                                        val text = notam.optString("all", "Sem texto disponível")
                                        resultado.append("ID: $id\n$text\n\n")
                                    }

                                    runOnUiThread {
                                        txtResultado.text = resultado.toString()
                                    }
                                } else {
                                    runOnUiThread {
                                        txtResultado.text = "Nenhum NOTAM encontrado para $icaoCode."
                                    }
                                }
                            } catch (e: Exception) {
                                runOnUiThread {
                                    txtResultado.text = "Erro ao processar dados: ${e.message}"
                                }
                            }
                        } else {
                            runOnUiThread {
                                txtResultado.text = "Erro: resposta vazia ou nula"
                            }
                        }
                    }
                }
            }
        })
    }
}
