package com.example.mytictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button;

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        var start_button = findViewById<Button>(R.id.start_button)

        start_button.setOnClickListener {
           val intent = Intent(this, TicTacToe::class.java)
            startActivity(intent)
        }
    }
}