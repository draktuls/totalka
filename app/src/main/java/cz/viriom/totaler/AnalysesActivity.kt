package cz.viriom.totaler

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cz.viriom.totaler.action.analysisMenu
import cz.viriom.totaler.helpers.MainHelper
import cz.viriom.totaler.history.AppDatabase
import cz.viriom.totaler.helpers.MainHelper.Companion
import java.util.*
import java.util.concurrent.Executors


class AnalysesActivity : AppCompatActivity() {

    private lateinit var clearbtn : Button
    private lateinit var db : AppDatabase
    private lateinit var layout : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyses)
        db = AppDatabase(this)

        clearbtn = findViewById(R.id.clear)
        layout = findViewById(R.id.linear);

        val scrollView = ScrollView(this)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            )
        scrollView.layoutParams = layoutParams

        val linearLayout = LinearLayout(this)
        val linearParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = linearParams

        scrollView.addView(linearLayout)
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())

        fun doFillTask(){
            myExecutor.execute {
                var history = db.HistoryDao().getAll()

                if(history.isEmpty()) {
                    myHandler.post {
                        var emptyview = MainHelper.createRecordText("You haven't analyzed any URL so far!",this)
                        linearLayout.addView(emptyview)
                    }
                }
                myHandler.post {
                    history.forEach{
                        val uid = it.analysis_id
                        val url = it.url
                        val date = java.time.format.DateTimeFormatter.ISO_INSTANT
                            .format(java.time.Instant.ofEpochSecond(it.date.toLong()))
                        var id = MainHelper.createRecordText("Date of analysis: ${date.toString()}\nAnalysis for: $url",this)
                        id.setOnClickListener{
                            var intent = Intent(AnalysesActivity@this, AnalysisActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.putExtra("analysis_id",uid)
                            startActivity(intent)
                            return@setOnClickListener
                        }
                        id.setOnLongClickListener(){
                            val analysisMenu = analysisMenu()
                            analysisMenu.startActionMode(id, R.menu.analysis_menu, "Title", "Subtitle")
                            id.isSelected = true
                            return@setOnLongClickListener true
                        }
                        id.setBackgroundResource(R.drawable.border)
                        linearLayout.addView(id)
                    }
                }
            }
        }
        doFillTask()
        layout.addView(scrollView)

        clearbtn.setOnClickListener(){
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setTitle("Are you sure?")
            builder.setMessage("This action will delete every entry in the current database!")
            builder.setPositiveButton(android.R.string.ok
            ) { _,
                _ ->
                val text = "History was deleted!"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
                Thread {
                    db.HistoryDao().nuke()
                    db.AnalysisDao().nuke()
                }.start()
                var intent = Intent(UrlActivity@ this, AnalysesActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish();
            }
            builder.setNegativeButton(android.R.string.cancel
            ) { _, _ -> }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}