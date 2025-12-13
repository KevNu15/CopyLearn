package com.example.copylearn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * Pantalla principal con dos botones:
 */
class MainActivity : AppCompatActivity() {

    private lateinit var btnNew: Button
    private lateinit var btnList: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnNew = findViewById(R.id.btnNewDocument)
        btnList = findViewById(R.id.btnDocumentList)

        btnNew.setOnClickListener {
            startActivity(Intent(this, DocumentActivity::class.java))
        }

        btnList.setOnClickListener {
            startActivity(Intent(this, DocumentListActivity::class.java))
        }
    }
}