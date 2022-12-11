package cz.viriom.totaler.key

import android.content.Context
import android.content.SharedPreferences

class KeyManagement(var context: Context) {
    val settings = context.getSharedPreferences("main", Context.MODE_PRIVATE)

    fun setKey(value: String) {
        val edit: SharedPreferences.Editor = settings.edit()
        edit.putString("API_KEY", value)
        edit.apply()
    }

    fun clearKey() {
        val edit: SharedPreferences.Editor = settings.edit()
        edit.putString("API_KEY", "")
        edit.apply()
    }

    fun getKey(): String? {
        return settings.getString("API_KEY", null)
    }
}