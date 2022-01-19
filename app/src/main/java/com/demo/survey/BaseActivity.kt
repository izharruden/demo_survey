package com.demo.survey

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


open class BaseActivity : AppCompatActivity() {

    companion object {
        var createToast: Toast? = null

        fun showToast(context: Context?, message: String) {
            if (context != null) {
                try {
                    if (createToast != null) createToast!!.cancel()
                    createToast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                    createToast?.show()
                } catch (ignored: Exception) {
                }
            }
        }

        fun hideKeyboardFrom(activity: Activity) {
            val imm: InputMethodManager =
                activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }
}