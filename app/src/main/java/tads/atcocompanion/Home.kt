package tads.atcocompanion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Home : AppCompatActivity() {

    lateinit var btMetar: Button
    lateinit var btNotam: Button
    lateinit var btCartas: Button
    lateinit var btRotaer: Button
    lateinit var btEscala: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btMetar = findViewById(R.id.btMetar)
        btMetar.setOnClickListener {
            val intent = Intent(this, Metar::class.java)
            startActivity(intent)
        }

        btNotam = findViewById(R.id.btNOTAM)
        btNotam.setOnClickListener {
            val intent = Intent(this, Notam::class.java)
            startActivity(intent)
        }

        btCartas = findViewById(R.id.btCartas)
        btCartas.setOnClickListener {
            Toast.makeText(this, "Função em desenvolvimento", Toast.LENGTH_SHORT).show()
        }

        btRotaer = findViewById(R.id.btRotaer)
        btRotaer.setOnClickListener {
            Toast.makeText(this, "Função em desenvolvimento", Toast.LENGTH_SHORT).show()
        }


    }
}
