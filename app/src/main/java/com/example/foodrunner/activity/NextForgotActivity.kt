package com.example.foodrunner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.foodrunner.R

class NextForgotActivity : AppCompatActivity() {
    lateinit var txMobileNumber:TextView
    lateinit var txEmail:TextView
    var MobileNumber:String?=""
    var Email:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next_forgot)
        txMobileNumber=findViewById(R.id.txMobileNumber)
        txEmail=findViewById(R.id.txEmail)
        if(intent!=null) {
            MobileNumber=intent.getStringExtra("MobileNumber")
            Email=intent.getStringExtra("Email")
        }
        txEmail.text=Email
        txMobileNumber.text=MobileNumber
    }
}