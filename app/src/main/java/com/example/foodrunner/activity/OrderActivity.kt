package com.example.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.foodrunner.R

class OrderActivity : AppCompatActivity() {
    lateinit var btnOk:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        btnOk=findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            val intent= Intent(this@OrderActivity,HomeActivity::class.java)
            startActivity(intent)
        }
    }
}