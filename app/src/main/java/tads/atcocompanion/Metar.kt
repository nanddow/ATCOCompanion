package tads.atcocompanion

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Metar : AppCompatActivity() {

    private val apiKey = "XIPvGSxEr1958vODJcRymRKp88hu081aRSZpZ8KU"
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metar)

        val btBuscaMetar = findViewById<Button>(R.id.btbuscametar)
        val editIcaoCode = findViewById<EditText>(R.id.editIcaoCode)
        val txtResultado = findViewById<TextView>(R.id.txtResultado)

        btBuscaMetar.setOnClickListener {
            // Esconde o teclado
            esconderTeclado()

            val icaoCode = editIcaoCode.text.toString().uppercase()
            if (icaoCode.isNotBlank()) {
                txtResultado.text = "Buscando METAR..."
                buscarMetar(icaoCode, txtResultado)
            } else {
                txtResultado.text = "Por favor, insira um código ICAO válido."
            }
        }
    }

    private fun esconderTeclado() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

    private fun buscarMetar(icaoCode: String, txtResultado: TextView) {
        val dateFormat = SimpleDateFormat("yyyyMMddHH", Locale.getDefault())
        val currentTime = dateFormat.format(Date())

        val url = "https://api-redemet.decea.mil.br/mensagens/metar/$icaoCode?api_key=$apiKey&data_ini=$currentTime&data_fim=$currentTime"

        Log.d("METAR", "URL gerada: $url")

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    txtResultado.text = "Erro ao buscar METAR: ${e.message}"
                }
                Log.e("METAR", "Erro na requisição: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        txtResultado.text = "Falha na resposta: ${response.message}"
                    }
                    Log.e("METAR", "Falha na resposta: ${response.message}")
                    return
                }

                val responseData = response.body?.string()
                Log.d("METAR", "Resposta bruta: $responseData")

                if (responseData != null) {
                    try {
                        val jsonResponse = JSONObject(responseData)
                        val data = jsonResponse.getJSONObject("data")
                        val metarList = data.getJSONArray("data")

                        if (metarList.length() > 0) {
                            val metarInfo = metarList.getJSONObject(0)
                            val metar = metarInfo.getString("mens")

                            runOnUiThread {
                                txtResultado.text = "$metar"
                            }
                        } else {
                            runOnUiThread {
                                txtResultado.text = "METAR não disponível para o código ICAO fornecido."
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            txtResultado.text = "Erro ao processar a resposta. Verifique o log."
                        }
                        Log.e("METAR", "Erro ao processar JSON: ${e.message}")
                    }
                } else {
                    runOnUiThread {
                        txtResultado.text = "Erro: Resposta vazia"
                    }
                }
            }
        })
    }
}
