package com.example.foodrunner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.foodrunner.R

class NextRegisterActivity : AppCompatActivity() {
    lateinit var txtName:TextView
    lateinit var txtEmail:TextView
    lateinit var txtMobileNumber:TextView
    lateinit var txtAddress:TextView
    var name:String?=""
    var email:String?=""
    var mobile:String?=""
    var address:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next_register)
        txtName=findViewById(R.id.txName)
        txtEmail=findViewById(R.id.txEmail)
        txtMobileNumber=findViewById(R.id.txMobileNumber)
        txtAddress=findViewById(R.id.txAddress)
        if(intent!=null) {
            name=intent.getStringExtra("Name")
            email=intent.getStringExtra("Email")
            mobile=intent.getStringExtra("MobileNumber")
            address=intent.getStringExtra("Address")
        }
        txtName.text=name
        txtEmail.text=email
        txtAddress.text=address
        txtMobileNumber.text=mobile
    }
}