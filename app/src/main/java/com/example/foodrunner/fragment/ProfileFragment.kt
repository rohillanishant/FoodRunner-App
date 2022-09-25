package com.example.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject
import kotlin.math.log

class ProfileFragment : Fragment() {
    lateinit var txtName:TextView
    lateinit var txtPhoneNumber:TextView
    lateinit var txtEmail:TextView
    lateinit var txtAddress:TextView
    lateinit var txtUserId:TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_profile, container, false)
        txtName=view.findViewById(R.id.txtName)
        txtPhoneNumber= view.findViewById(R.id.txtPhoneNumber)
        txtEmail =view.findViewById(R.id.txtEmail)
        txtAddress=view.findViewById(R.id.txtAddress)
        txtUserId=view.findViewById(R.id.txtUserId)

        sharedPreferences = (activity as FragmentActivity).getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
        txtUserId.text="User id = ${sharedPreferences.getString("user_id",null)}"
        txtName.text=sharedPreferences.getString("name",null)
        txtPhoneNumber.text="+91-" + sharedPreferences.getString("mobile_number",null)
        txtEmail.text=sharedPreferences.getString("email",null)
        txtAddress.text=sharedPreferences.getString("address",null)

        return view
    }
}