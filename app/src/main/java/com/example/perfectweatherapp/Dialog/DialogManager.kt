package com.example.perfectweatherapp.Dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogManager {
    fun locationServicesDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Enable location?")
        dialog.setMessage("Location disabled, do you want enable location?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK"){
            _,_ ->
            listener.OnClick()
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Calcel"){
                _,_ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener{
        fun OnClick()
    }

} // диалог для включения гпс