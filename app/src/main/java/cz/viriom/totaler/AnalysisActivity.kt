package cz.viriom.totaler

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.jayway.jsonpath.JsonPath
import cz.viriom.totaler.helpers.MainHelper
import cz.viriom.totaler.history.AppDatabase
import java.util.concurrent.Executors

class AnalysisActivity : AppCompatActivity() {

    private lateinit var db : AppDatabase
    private lateinit var test2 : TextView
    private lateinit var results : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)
        db = AppDatabase(this)

        test2 = findViewById(R.id.state)
        results = findViewById(R.id.results2)
        val uid = intent.extras?.getLong("analysis_id") ?: 0

        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())

        fun doLoadJson(){
            myExecutor.execute {
                var analysis = db.AnalysisDao().getById(uid)
                //val raw = JsonPath.parse(analysis.analysis_json)
                //val test = raw.read<
                //        List<Map<String, Object>>>("$.data.attributes.results[*]")
                myHandler.post {
                    //var final = test.joinToString()
                    test2.text = analysis.analysis_json
                    results.text = MainHelper.CreateReport(analysis.analysis_json)
                }
            }
        }
        doLoadJson()

    }
}