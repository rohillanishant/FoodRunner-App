package com.example.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.foodrunner.R

class RegisterActivity : AppCompatActivity() {
    lateinit var etName:EditText
    lateinit var etEmail:EditText
    lateinit var etMobileNumber:EditText
    lateinit var etAddress:EditText
    lateinit var btnRegister:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title="Register Yourself"
        etName=findViewById(R.id.etName)
        etEmail=findViewById(R.id.etEmail)
        etMobileNumber=findViewById(R.id.etMobileNumber)
        etAddress=findViewById(R.id.etAddress)
        btnRegister=findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener() {
            val intent= Intent(this@RegisterActivity, NextRegisterActivity::class.java)
            intent.putExtra("Name",etName.text.toString())
            intent.putExtra("Email",etEmail.text.toString())
            intent.putExtra("MobileNumber",etMobileNumber.text.toString())
            intent.putExtra("Address",etAddress.text.toString())
            startActivity(intent)
        }

    }
}