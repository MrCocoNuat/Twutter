package com.codepath.apps.restclienttemplate.models

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.codepath.apps.restclienttemplate.R

class ComposeDialogFragment(val composeDialogListener : ComposeDialogListener) : DialogFragment(){
    lateinit var dialogView : View
    lateinit var dialog : AlertDialog

    // activity calling must implement this
    interface ComposeDialogListener{
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_compose, null)
        dialogView = view

        val viewCharCount = view.findViewById<TextView>(R.id.tvCharCount)
        val viewEditText = view.findViewById<EditText>(R.id.etComposeText)

        viewEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){}
            override fun afterTextChanged(p0: Editable){
                val size = p0.length
                if (size > 280){
                    viewCharCount.setTextColor(Color.RED)
                    this@ComposeDialogFragment.dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled =  false
                }
                else{
                    viewCharCount.setTextColor(Color.BLACK)
                    this@ComposeDialogFragment.dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = true
                }
                viewCharCount.text = "$size / 280"
            }
        })

        val builder = AlertDialog.Builder(requireActivity())

        val title = "Compose a Tweet"

        builder.setTitle(title)
            .setPositiveButton("Tweet"){_, _ ->
                composeDialogListener.onDialogPositiveClick(this)
            }
            .setNegativeButton("Cancel"){_,_ ->
                composeDialogListener.onDialogNegativeClick(this)
            }
            .setView(view)

        dialog =  builder.create()
        return dialog
    }
}