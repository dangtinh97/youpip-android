package org.youpip.app.dialog

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.TextView
import org.youpip.app.R
import java.util.*

class MessageActivityNotification(context:Context):Dialog(context) {
    private var content: TextView
    init {
        setContentView(R.layout.notification_message)
        window!!.setBackgroundDrawableResource(R.color.transparent)
        val width = context.resources.displayMetrics.widthPixels*0.85
        window!!.setLayout(width.toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        content = findViewById<TextView>(R.id.message)
    }

    fun showMessage(message:String)
    {
        content.text = message
        show()
        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                dismiss()
                t.cancel() // also just top the timer thread, otherwise, you may receive a crash report
            }
        }, 3000) // after 2 second (or 2000 miliseconds), the task will be active.


    }
}