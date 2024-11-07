package tads.atcocompanion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Home : AppCompatActivity() {

    lateinit var btMetar: Button
    lateinit var btNotam: Button
    lateinit var btCartas: Button
    lateinit var btRotaer: Button

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
            val intent = Intent(this, Cartas::class.java)
            startActivity(intent)
        }

        btRotaer = findViewById(R.id.btRotaer)
        btRotaer.setOnClickListener {
            val intent = Intent(this, Rotaer::class.java)
            startActivity(intent)
        }

     }
}
