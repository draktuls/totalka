package cz.viriom.totaler

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cz.viriom.totaler.communicator.ApiCommunicate
import cz.viriom.totaler.history.AppDatabase
import cz.viriom.totaler.key.KeyManagement
import java.util.concurrent.Executors

class UrlActivity : AppCompatActivity() {

    private lateinit var urlinput : EditText
    private lateinit var apiout : TextView
    private lateinit var scan : Button
    private lateinit var analysesbtn : Button
    private lateinit var backbtn : Button
    private lateinit var db : AppDatabase
    private lateinit var results : TextView
    private var lock : Boolean = false
    private fun String.isValidUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_url)
        val keymanage = KeyManagement(this)
        db = AppDatabase(this)

        urlinput = findViewById(R.id.urlinput)
        apiout = findViewById(R.id.state)
        scan = findViewById(R.id.scanbutton)
        analysesbtn = findViewById(R.id.analyses)
        backbtn = findViewById(R.id.back_scan)
        results = findViewById(R.id.results)
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())

        val api = ApiCommunicate(keymanage.getKey(),apiout,db,results)

        fun doMyTask(url: String){
            myExecutor.execute {
                lock = true
                api.ScanUrl(url)
                myHandler.post {
                    //Log.e("TEST","Setting to false")
                    lock = false
                }
            }
        }

        backbtn.setOnClickListener(){
            var intent = Intent(UrlActivity@this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish();
        }

        urlinput.setOnKeyListener(){ view: View, i: Int, keyEvent: KeyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) &&
                (i == KeyEvent.KEYCODE_ENTER)) {
                if(lock){
                    val text = "Analysing is in progress!"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(applicationContext, text, duration)
                    toast.show()
                    return@setOnKeyListener false
                }
                results.text = ""
                val url = urlinput.text.toString()
                if (!url.isValidUrl()) {
                    val text = "Invalid URL"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(applicationContext, text, duration)
                    toast.show()
                    return@setOnKeyListener false
                }
                doMyTask(url)
                return@setOnKeyListener true;
            }
            return@setOnKeyListener false;
        }

        scan.setOnClickListener() {
            if(lock){
                val text = "Analysing is in progress!"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
                return@setOnClickListener
            }
            results.text = ""
            val url = urlinput.text.toString()
            if (!url.isValidUrl()) {
                val text = "Invalid URL"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
                return@setOnClickListener
            }
            doMyTask(url)
        }

        analysesbtn.setOnClickListener(){
            var intent = Intent(UrlActivity@this, AnalysesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}