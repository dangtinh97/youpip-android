package org.youpip.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.youpip.app.R

class ChatAdapter(val callbackOnClick: (String) -> Unit): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private var listData = emptyList<String>()
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun setData(arrayList: List<String>)
    {
        listData = arrayList
    }
}