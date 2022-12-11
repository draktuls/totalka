package cz.viriom.totaler.helpers

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.jayway.jsonpath.JsonPath
import cz.viriom.totaler.R


class MainHelper {
    companion object {
        fun createRecordText(msg: String, cnt: Context): TextView {
            var txt = TextView(cnt)
            txt.gravity = Gravity.CENTER
            val lay = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lay.setMargins(0, 0, 0, 20)
            txt.layoutParams = lay
            txt.text = msg
            txt.textSize = 14F
            txt.setPadding(60, 60, 60, 60)
            txt.setBackgroundResource(R.drawable.border)
            return txt
        }

        fun CreateReport(json: String?): String {
            val context = JsonPath.parse(json)
            val harmless = context.read<Int>("data.attributes.stats.harmless")
            val malicious = context.read<Int>("data.attributes.stats.malicious")
            val suspicious = context.read<Int>("data.attributes.stats.suspicious")
            val undetected = context.read<Int>("data.attributes.stats.undetected")
            val timeout = context.read<Int>("data.attributes.stats.timeout")
            val sum = (harmless + malicious + suspicious + undetected + timeout)
            val sus_sum = (malicious + suspicious)
            return "Score : $sus_sum/$sum\nHarmless: $harmless\nMalicious: $malicious\nSuspicious: $suspicious\nUndetected: $undetected\nTimeout: $timeout\n"
        }
    }
}