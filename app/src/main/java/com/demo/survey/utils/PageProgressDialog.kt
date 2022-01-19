package com.demo.survey.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.demo.survey.R

class PageProgressDialog private constructor(context: Context) :
    Dialog(context, R.style.ProgressDialogStyle) {
    companion object {
        fun show(context: Context): PageProgressDialog? {
            val dialog = PageProgressDialog(context)
            dialog.setCancelable(false)
            dialog.addContentView(
                ProgressBar(context),
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            val activity = context as Activity?
            if (activity == null || activity.isFinishing) {
                return null
            }
            dialog.show()
            return dialog
        }
    }
}