package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myapplication.Classes.Utils
import com.example.myapplication.databinding.ActivityResultBinding

class Result : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        val layout = ActivityResultBinding.inflate(layoutInflater)
        setContentView(layout.root)

        val bundle: Bundle? = intent.extras

        val difficulty = bundle?.getString("difficulty") ?: Difficulty.HARD.string
        val time = bundle?.getDouble("time") ?: 0.0
        val moves = bundle?.getInt("moves") ?: 0

        layout.resultTime.text = Utils.getTimeStringFromDoble(time)
        layout.resultMoves.text = moves.toString()

        val restart = findViewById<Button>(R.id.restart)
        restart.setOnClickListener {
            val intent = Intent(this, Game::class.java)
            intent.putExtra("difficulty", difficulty)
            startActivity(intent)
        }

        val menu = findViewById<Button>(R.id.menu)
        menu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val share = findViewById<Button>(R.id.share)
        share.setOnClickListener{
            val message = """
                Acabo de terminar mi partida en BIG BRAIN con el resultado siguiente:
                    
                    Dificultad : $difficulty
                    Tiempo usado : ${Utils.getTimeStringFromDoble(time)}
                    Total de movimientos: $moves
                
                Ven a jugar conmigo!!!
            """.trimIndent()

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }

            startActivity(Intent.createChooser(intent, "Share to : "))
        }
    }


}