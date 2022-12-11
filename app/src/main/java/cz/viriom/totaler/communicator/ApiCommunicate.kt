package cz.viriom.totaler.communicator

import android.graphics.Color
import android.widget.TextView
import com.jayway.jsonpath.JsonPath
import cz.viriom.totaler.helpers.MainHelper
import cz.viriom.totaler.history.AppDatabase
import cz.viriom.totaler.history.entities.Analysis
import cz.viriom.totaler.history.entities.History
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ApiCommunicate() {
    private var api_key : String? = null
    private val client = OkHttpClient()
    private lateinit var outage : TextView
    private lateinit var db : AppDatabase
    private lateinit var results : TextView

    constructor(key: String?,out: TextView,dbc: AppDatabase,res: TextView) : this() {
        api_key = key
        outage = out
        db = dbc
        results = res
    }

    public fun ScanUrl(url: String){
        val final_url = java.net.URLEncoder.encode(url, "utf-8")

        val mediaType = "application/x-www-form-urlencoded".toMediaTypeOrNull()
        val body = "url=$final_url".toRequestBody(mediaType)
        val request = Request.Builder()
          .url("https://www.virustotal.com/api/v3/urls")
          .post(body)
          .addHeader("accept", "application/json")
          .addHeader("x-apikey", "$api_key")
          .addHeader("content-type", "application/x-www-form-urlencoded")
          .build()

        val call = client.newCall(request)
        val response = call.execute()

        if (!response.isSuccessful) throw IOException("Url scanning response was not successful")
        val json = response.body?.string()
        val context = JsonPath.parse(json)
        val analysis_id_json = context.read<String>("data.id")
        outage?.text = "Url has been scanned, getting analysis"
        outage?.setTextColor(Color.GREEN)
        GetAnalysisReport(url, analysis_id_json, 3000)
    }

    private fun GetAnalysisReport(url: String,id: String,delay: Long){

        if(delay > 60000){
            outage?.text = "Analysis was queued for too long!"
            outage?.setTextColor(Color.RED)
        }

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://www.virustotal.com/api/v3/analyses/$id")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("x-apikey", "$api_key")
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        if (!response.isSuccessful) throw IOException("Getting analysis response is not successful")
        val json = response.body?.string()
        val context = JsonPath.parse(json)
        val status = context.read<String>("data.attributes.status")
        if(status == "queued"){
            outage?.text = "Analysis is queued - waiting"
            outage?.setTextColor(Color.YELLOW)
            Thread.sleep(delay)
            GetAnalysisReport(url,id,delay*2)
            return
        }
        val analysesDao = db.AnalysisDao()
        val historyDao = db.HistoryDao()
        val date = context.read<Int>("data.attributes.date")
        var lastid = analysesDao.insertAll(Analysis(analysis_json = json))
        historyDao.insertAll(History(analysis_id = lastid[0], url = url, date = date))
        outage?.text = "Report has been stored in the database"
        outage?.setTextColor(Color.GREEN)
        val final: String = MainHelper.CreateReport(json)
        results?.text = final
    }
}