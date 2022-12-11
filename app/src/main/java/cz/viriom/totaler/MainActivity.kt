package cz.viriom.totaler

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cz.viriom.totaler.key.KeyManagement

class MainActivity : AppCompatActivity() {

    private lateinit var apitext : EditText
    private lateinit var apiout : TextView
    private lateinit var savebutton : Button
    private lateinit var clearbutton : Button
    private lateinit var scanbutton : Button
    var api = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val key_manage = KeyManagement(this)
        apiout = findViewById(R.id.api_output)
        apitext = findViewById(R.id.apikey)
        apitext.setText(key_manage.getKey())
        if(apitext.text.toString() == ""){
            apiout.text = "Enter Virustotal API key before scanning!"
            apiout.setTextColor(resources.getColor(R.color.teal_700))
        }
        savebutton = findViewById(R.id.save_button)
        clearbutton = findViewById(R.id.clear_button)
        scanbutton = findViewById(R.id.scan_url)

        clearbutton.setOnClickListener {
            val key = key_manage.getKey()
            if(key == "") {
                apiout.text = "API key is already empty!"
                apiout.setTextColor(resources.getColor(R.color.warning))
                return@setOnClickListener
            }
            key_manage.clearKey()
            apitext.setText("")
            apiout.text = "API key was cleared!"
            apiout.setTextColor(resources.getColor(R.color.success))
            return@setOnClickListener
        }

        savebutton.setOnClickListener {
            val key = apitext.text.toString().lowercase()
            if (key.contains("[!\\\"#\$%&'()*+,-./:;\\\\\\\\<=>?@\\\\[\\\\]^_`{|}~]".toRegex())) {
                apiout.text = "API key must not contain special characters!"
                apiout.setTextColor(resources.getColor(R.color.warning))
                return@setOnClickListener
            }
            if(key.length != 64){
                apiout.text = "Invalid API key length!"
                apiout.setTextColor(resources.getColor(R.color.warning))
                return@setOnClickListener
            }
            key_manage.setKey(apitext.text.toString())
            apiout.text = "API key was saved!"
            apiout.setTextColor(resources.getColor(R.color.success))
            return@setOnClickListener
        }

        scanbutton.setOnClickListener {
            val key = key_manage.getKey()
            //Log.e("TEST", key.toString())
            if(key == null) {
                apiout.text = "API key is empty!"
                apiout.setTextColor(resources.getColor(R.color.warning))
                return@setOnClickListener
            }
            var intent = Intent(MainActivity@this, UrlActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish();
        }
    }
}