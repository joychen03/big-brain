package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){
    lateinit var layout : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(1000)
        setTheme(R.style.Theme_MyApplication)

        super.onCreate(savedInstanceState)
        layout = ActivityMainBinding.inflate(layoutInflater)
        setContentView(layout.root)

        layout.btnFacil.setOnClickListener {
            val intent = Intent(this, Game::class.java)
            intent.putExtra(Difficulty.KEY.string, Difficulty.EASY.string)
            startActivity(intent)
        }

        layout.btnNormal.setOnClickListener {
            val intent = Intent(this, Game::class.java)
            intent.putExtra(Difficulty.KEY.string, Difficulty.NORMAL.string)
            startActivity(intent)
        }

        layout.btnDificil.setOnClickListener {
            val intent = Intent(this, Game::class.java)
            intent.putExtra(Difficulty.KEY.string, Difficulty.HARD.string)
            startActivity(intent)

        }

        layout.info.setOnClickListener {
            val text = """
                Choose the difficulty level to start the game
            """.trimIndent()

            val toast = Toast.makeText(this, text, Toast.LENGTH_LONG)
            toast.show()

        }

    }




}

