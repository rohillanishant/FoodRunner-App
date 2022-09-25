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

class RegisterActivity : AppCompatActivity() {
    lateinit var etName:EditText
    lateinit var etEmail:EditText
    lateinit var etMobileNumber:EditText
    lateinit var etAddress:EditText
    lateinit var btnRegister:Button
    lateinit var etPassword:EditText
    lateinit var etConfirmPassword:EditText
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etName=findViewById(R.id.etName)
        etEmail=findViewById(R.id.etEmail)
        etMobileNumber=findViewById(R.id.etMobileNumber)
        etAddress=findViewById(R.id.etAddress)
        btnRegister=findViewById(R.id.btnRegister)
        etPassword=findViewById(R.id.etPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        btnRegister.setOnClickListener() {
            if(etName.text.toString().isEmpty())
                Toast.makeText(this@RegisterActivity,"Enter Name",Toast.LENGTH_LONG).show()
            else if(etName.length()<4) {
                Toast.makeText(this@RegisterActivity,"Name should contain atleast 3 characters",Toast.LENGTH_LONG).show()
            }
            else if(etEmail.text.toString().isEmpty())
                Toast.makeText(this@RegisterActivity,"Enter Email Id",Toast.LENGTH_LONG).show()
            else if(etMobileNumber.text.toString().isEmpty())
                Toast.makeText(this@RegisterActivity,"Enter Mobile Number",Toast.LENGTH_LONG).show()
            else if(etMobileNumber.length()!=10) {
                Toast.makeText(this@RegisterActivity,"Mobile Number should have 10 digits",Toast.LENGTH_LONG).show()
            }
            else if(etAddress.text.toString().isEmpty())
                Toast.makeText(this@RegisterActivity,"Enter Delivery Address",Toast.LENGTH_LONG).show()
            else if(etPassword.text.toString().isEmpty())
                Toast.makeText(this@RegisterActivity,"Enter Password",Toast.LENGTH_LONG).show()
            else if(etPassword.text.toString()!=etConfirmPassword.text.toString())
                Toast.makeText(this@RegisterActivity,"Passwords doesn't match. Please try again!",Toast.LENGTH_LONG).show()
            else if (etPassword.length()<4) {
                Toast.makeText(this@RegisterActivity, "Password size should be more than 4", Toast.LENGTH_LONG).show()
            }
            else {
                val url="http://13.235.250.119/v2/register/fetch_result/"
                val queue=Volley.newRequestQueue(this@RegisterActivity)
                val jsonParams=JSONObject()
                jsonParams.put("name",etName.text.toString())
                jsonParams.put("mobile_number",etMobileNumber.text.toString())
                jsonParams.put("password",etPassword.text.toString())
                jsonParams.put("address",etAddress.text.toString())
                jsonParams.put("email",etEmail.text.toString())
                if (ConnectionManager().checkConnectivity(this@RegisterActivity)){
                    val jsonobjectRequest=object :JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                                val response = data.getJSONObject("data")
                                sharedPreferences.edit().putString("user_id", response.getString("user_id")).apply()
                                sharedPreferences.edit().putString("name", response.getString("name")).apply()
                                sharedPreferences.edit().putString("email", response.getString("email")).apply()
                                sharedPreferences.edit().putString("mobile_number", response.getString("mobile_number")).apply()
                                sharedPreferences.edit().putString("address", response.getString("address")).apply()
                                startActivity(Intent(this@RegisterActivity,HomeActivity::class.java))
                                Toast.makeText(this@RegisterActivity, "Successfully Registered", Toast.LENGTH_LONG).show()
                                finish ()
                            }else{
                                val error=data.getString("errorMessage")
                                Toast.makeText(this@RegisterActivity," ${error}",Toast.LENGTH_SHORT).show()
                            }
                        }catch(e:Exception){
                            Toast.makeText(this@RegisterActivity,"$e",Toast.LENGTH_SHORT).show()
                        } }, Response.ErrorListener {
                            Toast.makeText(this@RegisterActivity,"Volley Error!",Toast.LENGTH_SHORT).show()
                        }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "ea418ea70c78af"
                            return headers
                        }
                    }
                    queue.add(jsonobjectRequest)
                }else {
                    val dialog= AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Found")
                    dialog.setPositiveButton("Open Settings"){ text,listener->
                        val settingsIntent=Intent(Settings.ACTION_WIFI_SETTINGS)
                        startActivity(settingsIntent)
                        this.finish()
                    }
                    dialog.setNegativeButton("Exit"){ text,listener->
                        ActivityCompat.finishAffinity(this@RegisterActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }

    }
}