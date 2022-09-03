package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.foodrunner.R

class LoginActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtForgotPassword:TextView
    lateinit var txtSignUp:TextView
    lateinit var btnLogin:Button
    lateinit var etMobileNumber:EditText
    lateinit var etPassword:EditText
    val validMobileNumber="0123456789"
    val validPassword="foodrunner"
    override fun onCreate(savedInstanceState: Bundle?) {
        title="Login"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences=getSharedPreferences("FoodRunner Preferences",Context.MODE_PRIVATE)
        txtForgotPassword=findViewById(R.id.txtForgotPassword)
        txtSignUp=findViewById(R.id.txtSignUp)
        btnLogin=findViewById(R.id.btnLogin)
        etMobileNumber=findViewById(R.id.etMobileNumber)
        etPassword=findViewById(R.id.etPassword)
        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        if(isLoggedIn) {
            val intent=Intent(this@LoginActivity, NewActivity::class.java)
            startActivity(intent)
        }
        txtForgotPassword.setOnClickListener() {
            val intent= Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        txtSignUp.setOnClickListener() {
            val intent= Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnLogin.setOnClickListener() {
            if(validMobileNumber==etMobileNumber.text.toString() && validPassword==etPassword.text.toString()) {
                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                val intent= Intent(this@LoginActivity, NewActivity::class.java)
                startActivity(intent)
            } else if(validMobileNumber!=etMobileNumber.text.toString()) {
                Toast.makeText(this@LoginActivity,"This Mobile no is not registered with us",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@LoginActivity,"Incorrect Password",Toast.LENGTH_SHORT).show()
            }
        }
    }
}