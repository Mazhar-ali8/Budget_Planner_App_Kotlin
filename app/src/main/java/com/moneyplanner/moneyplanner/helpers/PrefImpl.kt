package com.moneyplanner.moneyplanner.helpers

import android.R.attr.data
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PrefImpl(private val context: Context): IPref {
    val SHARED_PREFERENCES_ID= "lelooo"

    private val pref: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_ID, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    override fun put(key: String, value: Any) {
        when (value) {
            is String -> editor.putString(key, value).apply()
            is Int -> editor.putInt(key, value).apply()
            is Float -> editor.putFloat(key, value).apply()
            is Long -> editor.putLong(key, value).apply()
            is Boolean -> editor.putBoolean(key, value).apply()
            else -> false
        }
    }

    override fun str(key: String, default: String): String = pref.getString(key, default) ?: ""

    override fun long(key: String, default: Long): Long = pref.getLong(key, default)

    override fun float(key: String, default: Float): Float = pref.getFloat(key, default)

    override fun int(key: String, default: Int): Int = pref.getInt(key, default)

    override fun bool(key: String, default: Boolean): Boolean = pref.getBoolean(key, default)

    override fun <T> putObject(key: String, value: T) {

        val gson: Gson =Gson()
        val jsonString:String=gson.toJson(value)
        put(key,jsonString)
    }

     override fun <T> getObject(key: String, jsonObject:Class<T>): T? {

         try {
             val gson: Gson =Gson()
             val jsonString:String=str(key)
             val tObject:T=gson.fromJson(jsonString,jsonObject)
             return tObject
         } catch (e: JsonSyntaxException) {
             Log.e("TAG", "getObject: ${e.localizedMessage}" )
             return null
         }

     }

    override fun putStringArrayList(key: String, value: ArrayList<String>) {
        //Set the values
        val gson = Gson()
        val json: String = gson.toJson(value)
        editor.putString(key, json)
        editor.apply()

    }

    override fun getStringArrayList(key: String): ArrayList<String?>? {
        val gson = Gson()
        val json: String = pref.getString(key, null)!!
        val type: Type = object : TypeToken<java.util.ArrayList<String?>?>() {}.getType()
        return gson.fromJson(json, type)
    }


    override fun clear() = pref.edit().clear().apply()
}