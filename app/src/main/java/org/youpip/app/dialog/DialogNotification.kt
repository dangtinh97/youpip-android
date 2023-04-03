package org.youpip.app.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import org.youpip.app.R
import java.util.*

class DialogNotification(context:Context):Dialog(context) {
    private var content: TextView
    private var btnConfirm:MaterialButton
    private var btnCancel:MaterialButton
    init {
        setContentView(R.layout.dialog_notification)
        window!!.setBackgroundDrawableResource(R.color.transparent)
        val width = context.resources.displayMetrics.widthPixels*0.85
        window!!.setLayout(width.toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        content = findViewById<TextView>(R.id.content)
        btnCancel = findViewById(R.id.cancel)
        btnConfirm = findViewById(R.id.confirm)

        btnCancel.setOnClickListener {
            dismiss()
        }


    }

    fun showMessage(message:String,callbackOnClick: (Boolean) -> Unit)
    {
        content.text = message
        show()
        btnConfirm.setOnClickListener {
            callbackOnClick(true)
        }
    }
}