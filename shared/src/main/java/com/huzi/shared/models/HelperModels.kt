package com.huzi.shared.models

import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime

data class SnackbarMessage(val message: String?,
                           val isError: Boolean = true,
                           @BaseTransientBottomBar.Duration
                           var length: Int = Snackbar.LENGTH_SHORT)