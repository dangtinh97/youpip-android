package org.youpip.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.youpip.app.R
import org.youpip.app.model.ListChatModel

class ChatAdapter(val callbackOnClick: (ListChatModel) -> Unit): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private var listData = emptyList<ListChatModel>()
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.full_name)
        val mess: TextView = itemView.findViewById<TextView>(R.id.content)
        val layout: RelativeLayout = itemView.findViewById<RelativeLayout>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mess.text = listData[position].message
        holder.name.text = listData[position].fullName
        holder.layout.setOnClickListener {
            callbackOnClick(listData[position])
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun setData(arrayList: List<ListChatModel>)
    {
        listData = arrayList
        notifyDataSetChanged()
    }
}