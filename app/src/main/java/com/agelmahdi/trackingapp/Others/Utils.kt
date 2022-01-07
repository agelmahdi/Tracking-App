package com.agelmahdi.trackingapp.Others

import android.content.Context
import android.icu.text.CaseMap
import com.agelmahdi.trackingapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object Utils {
    fun showCancelDialog(context: Context, title: String, message: String) : MaterialAlertDialogBuilder{

        val dialog = MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.ic_delete)
            .setNegativeButton("No"){ dialogInterface,_ ->
                dialogInterface.cancel()
            }

        return dialog
    }
}