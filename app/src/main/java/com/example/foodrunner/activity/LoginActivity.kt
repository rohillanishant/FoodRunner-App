package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtForgotPassword:TextView
    lateinit var txtSignUp:TextView
    lateinit var btnLogin:Button
    lateinit var etMobileNumber:EditText
    lateinit var toolbar: Toolbar
    lateinit var etPassword:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Login"

        sharedPreferences=getSharedPreferences("FoodRunner Preferences",Context.MODE_PRIVATE)
        txtForgotPassword=findViewById(R.id.txtForgotPassword)
        txtSignUp=findViewById(R.id.txtSignUp)
        btnLogin=findViewById(R.id.btnLogin)
        etMobileNumber=findViewById(R.id.etMobileNumber)
        etPassword=findViewById(R.id.etPassword)
        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        if(isLoggedIn) {
            val intent=Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
        }
        txtForgotPassword.setOnClickListener() {
            val intent= Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        txtSignUp.setOnClickListener() {
            val intent= Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener() {
            val queue= Volley.newRequestQueue(this@LoginActivity)
            val url="http://13.235.250.119/v2/login/fetch_result/"
            val jsonParams= JSONObject()
            jsonParams.put("mobile_number",etMobileNumber.text.toString())
            jsonParams.put("password",etPassword.text.toString())
            if(ConnectionManager().checkConnectivity(this@LoginActivity)) {
                val jsonRequest=object: JsonObjectRequest(Request.Method.POST,url,jsonParams, Response.Listener{
                    try {
                        val data=it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if(success) {
                            sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                            val loginObject=data.getJSONObject("data")
                            sharedPreferences.edit().putString("user_id",loginObject.getString("user_id")).apply()
                            sharedPreferences.edit().putString("name",loginObject.getString("name")).apply()
                            sharedPreferences.edit().putString("mobile_number",loginObject.getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("email",loginObject.getString("email")).apply()
                            sharedPreferences.edit().putString("address",loginObject.getString("address")).apply()
                            val intent= Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@LoginActivity,"Please Enter Correct Credentials ",Toast.LENGTH_SHORT).show()
                        }
                    } catch (e:Exception) {
                        Toast.makeText(this@LoginActivity,"Some Error occurred",Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this@LoginActivity,"Volley Error occurred",Toast.LENGTH_SHORT).show()
                } ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers=HashMap<String ,String>()
                        headers["content-type"]="application/json"
                        headers["token"]="ea418ea70c78af"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            } else {
                val dialog= AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings"){ text,listener->
                    val settingsIntent=Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(settingsIntent)
                    this.finish()
                }
                dialog.setNegativeButton("Exit"){ text,listener->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }
}