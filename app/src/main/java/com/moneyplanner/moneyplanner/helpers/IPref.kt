package com.moneyplanner.moneyplanner.helpers

interface IPref {

    fun put(key: String, value: Any)
    fun str(key: String, default: String = ""): String
    fun long(key: String, default: Long = -1): Long
    fun float(key: String, default: Float = -1f): Float
    fun int(key: String, default: Int = -1): Int
    fun bool(key: String, default: Boolean = false): Boolean
    fun <T>putObject(key : String,value: T)
    fun <T> getObject(key: String, jsonObject: Class<T>) :T?

    fun putStringArrayList(key : String,value:ArrayList<String>)
    fun getStringArrayList(key : String):ArrayList<String?>?


    fun clear()


}
