package io.github.webcurate.clients

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import io.github.webcurate.R

object DialogueMaker {

    var alertDialog: AlertDialog? = null

    @SuppressLint("InflateParams")
    fun startLoading(context: Context) {
        alertDialog = AlertDialog.Builder(context)
            .setView(
                LayoutInflater.from(context).inflate(R.layout.loading_view, null)
            )
            .setCancelable(true).create()
        if(alertDialog!=null) alertDialog?.show()
    }

    fun stopLoading() {
        if(alertDialog!=null)  {
            alertDialog?.dismiss()
        }
    }
}