package com.huzi.shared.util

import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class StringPrefs(private val keyValue: String,private val defaultValue : String?= null)  : ReadWriteProperty<PrefsImpl, String?> {
    override fun getValue(thisRef: PrefsImpl, property: KProperty<*>): String? {
        return thisRef.sharedPreferences.getString(keyValue,defaultValue)
    }

    override fun setValue(thisRef: PrefsImpl, property: KProperty<*>, value: String?) {
        thisRef.sharedPreferences.edit {
            putString(keyValue, value).apply()
        }
    }
}