package com.huzi.shared.common

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.huzi.shared.R
import com.huzi.shared.extensions.postDelayed
import com.huzi.shared.extensions.visible

@DslMarker
internal annotation class SimpleDialogMaker

@SimpleDialogMaker
class SimpleDialogBuilder(private val context: Context?) {

    @StringRes
    var titleRes: Int? = null
    var title: String? = null

    @StringRes
    var messageRes: Int? = null
    var message: String? = null

    @StringRes
    var positiveButtonTextRes: Int? = null
    var positiveButtonText: String? = null
    var isPositiveButtonEnabled = true

    @StringRes
    var negativeButtonTextRes: Int? = null
    var negativeButtonText: String? = null
    var isNegativeButtonEnabled = true


    var onConfirm: (() -> Unit)? = null
    var onCancel: (() -> Unit)? = null
    var onDismiss: (() -> Unit)? = null

    //Views
    private lateinit var rootView: View
    private var dialog: BottomSheetDialog? = null

    private val titleTv: TextView by lazy {
        rootView.findViewById(R.id.simple_title)
    }

    private val messageTv: TextView by lazy {
        rootView.findViewById(R.id.simple_message)
    }

    private val confirmBtn: MaterialButton by lazy {
        rootView.findViewById(R.id.btn_simple_confirm)
    }

    private val cancelBtn: MaterialButton by lazy {
        rootView.findViewById(R.id.btn_simple_cancel)
    }

    fun build(): BottomSheetDialog? {
        val ctx = context ?: return null
        dialog = BottomSheetDialog(ctx)
        dialog?.let { dlg ->
            val inflater = dlg.layoutInflater
            rootView = inflater.inflate(R.layout.dialog_simple, FrameLayout(ctx))
            dlg.setContentView(rootView)
        }
        setupViews()
        return dialog
    }

    private fun setupViews() {
        setupTitle()
        setupMessage()
        setupPositiveButton()
        setupNegativeButton()
        dialog?.setOnDismissListener {
            onDismiss?.invoke()
        }
    }

    private fun setupTitle() {
        if (title == null && titleRes == null) {
            titleTv.visible(false)
            return
        }
        title?.let {
            titleTv.text = it
            return
        }
        titleRes?.let {
            titleTv.setText(it)
        }
    }

    private fun setupMessage() {
        if (message == null && messageRes == null) {
            messageTv.visible(false)
            return
        }
        message?.let {
            messageTv.text = it
            return
        }
        messageRes?.let {
            messageTv.setText(it)
        }
    }

    private fun setupPositiveButton() {

        if (!isPositiveButtonEnabled) {
            confirmBtn.visible(false)
            return
        }

        confirmBtn.setOnClickListener {
            dialog?.dismiss()
            postDelayed(250) {
                onConfirm?.invoke()
            }
        }
        positiveButtonText?.let {
            confirmBtn.text = it
            return
        }
        positiveButtonTextRes?.let {
            confirmBtn.setText(it)
        }
    }

    private fun setupNegativeButton() {
        if (!isNegativeButtonEnabled) {
            cancelBtn.visible(false)
            return
        }

        cancelBtn.setOnClickListener {
            dialog?.dismiss()
            postDelayed(250) {
                onCancel?.invoke()
            }
        }
        negativeButtonText?.let {
            cancelBtn.text = it
            return
        }
        negativeButtonTextRes?.let {
            cancelBtn.setText(it)
        }
    }
}

fun simpleDialog(context: Context?, init: SimpleDialogBuilder.() -> Unit) =
        SimpleDialogBuilder(context).also(init).build()

