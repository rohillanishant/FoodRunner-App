package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etMobileNumber:EditText
    lateinit var etEmail:EditText
    lateinit var btnNext:Button
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etMobileNumber=findViewById(R.id.etMobileNumber)
        etEmail=findViewById(R.id.etEmail)
        btnNext=findViewById(R.id.btnNext)
        
        btnNext.setOnClickListener() {
            if(etMobileNumber.text.toString().isEmpty()) {
                Toast.makeText(this@ForgotPasswordActivity,"Mobile Number not Entered",Toast.LENGTH_SHORT).show()
            }else if(etEmail.text.toString().isEmpty()) {
                Toast.makeText(this@ForgotPasswordActivity,"Email Id not Entered",Toast.LENGTH_SHORT).show()
            } else if(etMobileNumber.text.toString().length!=10) {
                Toast.makeText(this@ForgotPasswordActivity,"Enter valid mobile number",Toast.LENGTH_SHORT).show()
            } else {
                val queue= Volley.newRequestQueue(this@ForgotPasswordActivity)
                val url="http://13.235.250.119/v2/forgot_password/fetch_result"
                val jsonParams= JSONObject()
                jsonParams.put("mobile_number",etMobileNumber.text.toString())
                jsonParams.put("email",etEmail.text.toString())
                if(ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {
                    val jsonRequest=object: JsonObjectRequest(Request.Method.POST,url,jsonParams, Response.Listener{
                        try {
                            val data=it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if(success) {
                                if(data.getBoolean("first_try")) {
                                    Toast.makeText(this@ForgotPasswordActivity,"OTP is sent to your registered email for the first time",Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@ForgotPasswordActivity,"OTP is sent to your email ,if not please use previous OTP",Toast.LENGTH_SHORT).show()
                                }
                                val intent= Intent(this@ForgotPasswordActivity, OTPActivity::class.java)
                                intent.putExtra("mobile_number",etMobileNumber.text.toString())
                                startActivity(intent)
                            } else {
                                val error=data.getString("errorMessage")
                                Toast.makeText(this@ForgotPasswordActivity,"$error ",Toast.LENGTH_SHORT).show()
                            }
                        } catch (e:Exception) {
                            Toast.makeText(this@ForgotPasswordActivity,"$e",Toast.LENGTH_SHORT).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(this@ForgotPasswordActivity,"Volley Error occurred",Toast.LENGTH_SHORT).show()
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
                    val dialog= AlertDialog.Builder(this@ForgotPasswordActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not  Found")
                    dialog.setPositiveButton("Open Settings"){ text,listener->
                        val settingsIntent=Intent(Settings.ACTION_WIFI_SETTINGS)
                        startActivity(settingsIntent)
                        this.finish()
                    }
                    dialog.setNegativeButton("Exit"){ text,listener->
                        ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }
}