package tads.atcocompanion

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class Rotaer : AppCompatActivity() {

    private val apiToken =
        "155d7ed1bc1ddc28ef76f305eac3f595b301227d9aece1387a311c631130285495bd7c17133aa777f08850af9a92ff77"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotaer)

        val btbuscarotaer = findViewById<Button>(R.id.btbuscarotaer)
        val editIcaoCode = findViewById<EditText>(R.id.editIcaoCode)
        val txtResultado = findViewById<TextView>(R.id.txtResultado)

        btbuscarotaer.setOnClickListener {
            val icaoCode = editIcaoCode.text.toString().uppercase()

            if (icaoCode.isNotBlank()) {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                // Exibir mensagem de carregamento
                txtResultado.text = "Buscando informações do aeroporto..."

                // Chamar a função de buscar os dados
                buscarRotaer(icaoCode, txtResultado)
            } else {
                txtResultado.text = "Por favor, insira um código ICAO válido."
            }
        }
    }

    private fun buscarRotaer(icaoCode: String, txtResultado: TextView) {
        // Iniciar a chamada assíncrona
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resultado = withContext(Dispatchers.IO) {
                    // Chamar a API usando HttpURLConnection
                    val urlString =
                        "https://airportdb.io/api/v1/airport/$icaoCode?apiToken=$apiToken"
                    val url = URL(urlString)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000

                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()

                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    reader.close()

                    // Retorna a resposta da API como string
                    stringBuilder.toString()
                }

                // Exibir o JSON para depuração
                println("Resposta da API: $resultado")

                // Processa o resultado
                processarResposta(resultado, txtResultado)

            } catch (e: Exception) {
                txtResultado.text = "Erro ao buscar dados do aeroporto."
            }
        }
    }

    private fun processarResposta(response: String, txtResultado: TextView) {
        try {
            val jsonResponse = JSONObject(response)

            val name = jsonResponse.optString("name", "Nome não encontrado")
            val icaoCode = jsonResponse.optString("icao_code", "Código ICAO não encontrado")
            val iataCode = jsonResponse.optString("iata_code", "Código IATA não encontrado")
            val city = jsonResponse.optString("municipality", "Cidade não encontrada")
            val country =
                jsonResponse.optJSONObject("country")?.optString("name", "País não encontrado")
            val region =
                jsonResponse.optJSONObject("region")?.optString("name", "Região não encontrada")
            val latitude = jsonResponse.optDouble("latitude_deg", 0.0)
            val longitude = jsonResponse.optDouble("longitude_deg", 0.0)
            val elevation = jsonResponse.optString("elevation_ft", "Elevação não encontrada")

            // Frequências
            val frequencies = jsonResponse.optJSONArray("freqs")
            val frequenciesText = if (frequencies != null && frequencies.length() > 0) {
                val freqList = mutableListOf<String>()
                for (i in 0 until frequencies.length()) {
                    val freqObj = frequencies.getJSONObject(i)
                    val description = freqObj.optString("description", "Sem descrição")
                    val frequency = freqObj.optString("frequency_mhz", "Sem frequência")
                    freqList.add("${description.padEnd(25)}: $frequency MHz")
                }
                freqList.joinToString("\n")
            } else {
                "Sem frequências disponíveis"
            }

            // Pistas (Runways)
            val runways = jsonResponse.optJSONArray("runways")
            val runwaysText = if (runways != null && runways.length() > 0) {
                val runwayList = mutableListOf<String>()
                for (i in 0 until runways.length()) {
                    val runwayObj = runways.getJSONObject(i)
                    val leIdent = runwayObj.optString("le_ident", "Sem identificação")
                    val heIdent = runwayObj.optString("he_ident", "Sem identificação")
                    val length = runwayObj.optString("length_ft", "Sem comprimento")
                    val width = runwayObj.optString("width_ft", "Sem largura")
                    val surface = runwayObj.optString("surface", "Sem superfície")
                    runwayList.add(
                        "${leIdent.padEnd(5)} / ${heIdent.padEnd(5)}: ${length.padEnd(8)} ft x ${
                            width.padEnd(
                                5
                            )
                        } ft - $surface"
                    )
                }
                runwayList.joinToString("\n")
            } else {
                "Sem pistas disponíveis"
            }

            // Auxílios à navegação (Navaids)
            val navaids = jsonResponse.optJSONArray("navaids")
            val navaidsText = if (navaids != null && navaids.length() > 0) {
                val navaidList = mutableListOf<String>()
                for (i in 0 until navaids.length()) {
                    val navaidObj = navaids.getJSONObject(i)
                    val name = navaidObj.optString("name", "Sem nome")
                    val type = navaidObj.optString("type", "Sem tipo")
                    val frequency = navaidObj.optString("frequency_khz", "Sem frequência")
                    navaidList.add("${name.padEnd(20)} | ${type.padEnd(10)} | $frequency kHz")
                }
                navaidList.joinToString("\n")
            } else {
                "Sem auxílios à navegação disponíveis"
            }

            // Formatando os dados
            val resultText = StringBuilder()
                .appendLine("Nome:".padEnd(20) + name)
                .appendLine("ICAO Code:".padEnd(20) + icaoCode)
                .appendLine("IATA Code:".padEnd(20) + iataCode)
                .appendLine("Localização:".padEnd(20) + "$city, $region, $country")
                .appendLine("Coordenadas:".padEnd(20) + "Latitude $latitude, Longitude $longitude")
                .appendLine("Elevação:".padEnd(20) + "$elevation ft")
                .appendLine()
                .appendLine("Frequências:")
                .appendLine(frequenciesText)
                .appendLine()
                .appendLine("Pistas:")
                .appendLine(runwaysText)
                .appendLine()
                .appendLine("Auxílios à Navegação:")
                .appendLine(navaidsText)
                .toString()

            runOnUiThread {
                txtResultado.text = resultText
            }
        } catch (e: Exception) {
            txtResultado.text = "Erro ao processar os dados. Verifique o formato da resposta."
            println("Erro ao processar os dados: ${e.localizedMessage}")
        }
    }
}
