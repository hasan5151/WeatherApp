package com.huzi.shared.extensions

import android.text.Editable
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.postDelayed
import androidx.core.widget.doAfterTextChanged

val Editable?.text: String?
    get() {
        return if (this.isNullOrBlank()) null
        else this.toString()
    }