package org.youpip.app.utils

import android.content.Context

class MySharePre(context: Context) {
    private val sharePreferences = context.getSharedPreferences(context.packageName+"_preferences", Context.MODE_PRIVATE)
    private val editor = sharePreferences.edit()

    fun saveString(key: String, value: String){
        editor.putString(key, value)
        editor.commit()
    }
    fun getString(key: String): String? {
        return sharePreferences.getString(key, null)
    }

    fun saveBoolean(key: String, value: Boolean){
        editor.putBoolean(key, value)
        editor.commit()
    }
    fun getBoolean(key: String): Boolean{
        return sharePreferences.getBoolean(key,false)
    }

    fun saveInt(key:String,value: Int){
        editor.putInt(key,value)
        editor.commit()
    }

    fun getInt(key: String):Int
    {
        return sharePreferences.getInt(key,0)
    }

    fun remove(key: String){
        editor.remove(key)
        editor.commit()
    }
}