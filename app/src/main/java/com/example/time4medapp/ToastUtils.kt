package com.example.time4medapp

import android.app.Activity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast

object ToastUtils {

    fun showCustomToast(activity: Activity, message: String) {

        val inflater: LayoutInflater = activity.layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val text = layout.findViewById<TextView>(R.id.toast_text)
        text.text = message

        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}