package com.example.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject

class OTPActivity : AppCompatActivity() {
    lateinit var etOtp:EditText
    lateinit var etNewPassword:EditText
    lateinit var etConfirmPassword:EditText
    lateinit var btnSubmit:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        etOtp=findViewById(R.id.etOtp)
        etNewPassword=findViewById(R.id.etNewPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)
        btnSubmit=findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener{
            if(etOtp.text.toString().isEmpty()) {
                Toast.makeText(this@OTPActivity,"Please Enter OTP", Toast.LENGTH_SHORT).show()
            }else if(etNewPassword.text.toString().isEmpty()) {
                Toast.makeText(this@OTPActivity,"Please Enter New Password", Toast.LENGTH_SHORT).show()
            } else if(etNewPassword.text.toString().length<=4) {
                Toast.makeText(this@OTPActivity,"Password should contain more than 4 characters", Toast.LENGTH_SHORT).show()
            }else if(etNewPassword.text.toString()!=etConfirmPassword.text.toString()) {
                Toast.makeText(this@OTPActivity,"Passwords do not match", Toast.LENGTH_SHORT).show()
            }
            else {
                val queue= Volley.newRequestQueue(this@OTPActivity)
                val url="http://13.235.250.119/v2/reset_password/fetch_result"
                val jsonParams= JSONObject()
                jsonParams.put("mobile_number",intent.getStringExtra("mobile_number"))
                jsonParams.put("otp",etOtp.text.toString())
                jsonParams.put("password",etNewPassword.text.toString())
                if(ConnectionManager().checkConnectivity(this@OTPActivity)) {
                    val jsonRequest=object: JsonObjectRequest(Request.Method.POST,url,jsonParams, Response.Listener{
                        try {
                            val data=it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if(success) {
                                val successMessage=data.getString("successMessage")
                                Toast.makeText(this@OTPActivity,"$successMessage", Toast.LENGTH_SHORT).show()
                                val intent= Intent(this@OTPActivity, LoginActivity::class.java)
                                startActivity(intent)
                            } else {
                                val error=data.getString("errorMessage")
                                Toast.makeText(this@OTPActivity,"$error ", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e:Exception) {
                            Toast.makeText(this@OTPActivity,"$e", Toast.LENGTH_SHORT).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(this@OTPActivity,"Volley Error occurred", Toast.LENGTH_SHORT).show()
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
                    val dialog= AlertDialog.Builder(this@OTPActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings"){ text,listener->
                        val settingsIntent=Intent(Settings.ACTION_WIFI_SETTINGS)
                        startActivity(settingsIntent)
                        this.finish()
                    }
                    dialog.setNegativeButton("Exit"){ text,listener->
                        ActivityCompat.finishAffinity(this@OTPActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }
}