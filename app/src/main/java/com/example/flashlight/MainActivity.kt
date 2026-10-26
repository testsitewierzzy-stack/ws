package com.example.flashlight

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        
        val btn = Button(this)
        btn.text = "Включить ускорение"
        btn.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        
        layout.addView(btn)
        setContentView(layout)
    }
}