package com.huzi.shared.util

import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("unchecked_cast")
class BooleanPrefs (private val keyValue: String,private val defaultValue : Boolean = false) : ReadWriteProperty<PrefsImpl, Boolean>{

    override fun getValue(thisRef: PrefsImpl, property: KProperty<*>): Boolean {
        return thisRef.sharedPreferences.getBoolean(keyValue,defaultValue)
    }

    override operator fun setValue(thisRef: PrefsImpl, property: KProperty<*>, value: Boolean) {
        thisRef.sharedPreferences.edit {
            putBoolean(keyValue, value).apply()
        }
    }
}