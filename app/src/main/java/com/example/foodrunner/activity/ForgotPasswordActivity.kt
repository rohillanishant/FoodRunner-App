package com.example.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.foodrunner.R

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etMobileNumber:EditText
    lateinit var etEmail:EditText
    lateinit var btnNext:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title="Forgot Password"
        setContentView(R.layout.activity_forgot_password)
        etMobileNumber=findViewById(R.id.etMobileNumber)
        etEmail=findViewById(R.id.etEmail)
        btnNext=findViewById(R.id.btnNext)
        btnNext.setOnClickListener() {
            val intent= Intent(this@ForgotPasswordActivity, NextForgotActivity::class.java)
            intent.putExtra("MobileNumber",etMobileNumber.text.toString())
            intent.putExtra("Email",etEmail.text.toString())
            startActivity(intent)
        }
    }
}