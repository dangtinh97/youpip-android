package org.youpip.app.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.youpip.app.R
import org.youpip.app.model.MessageModel

class MessageAdapter(): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private var listData = ArrayList<MessageModel>()
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val messL = itemView.findViewById<TextView>(R.id.message_left)
        val messR = itemView.findViewById<TextView>(R.id.message_right)
        val layout = itemView.findViewById<RelativeLayout>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {

        if(listData[position].me){
            holder.messL.visibility = View.GONE
            holder.messR.visibility = View.VISIBLE
            holder.messR.text = listData[position].message
        }else{
            holder.messL.visibility = View.VISIBLE
            holder.messR.visibility = View.GONE
            holder.messL.text = listData[position].message
        }


    }

    override fun getItemCount(): Int {
        return listData.size
    }
    fun setData(data:ArrayList<MessageModel>){
        listData = data
        notifyDataSetChanged()
    }

    fun appendLast(mess:MessageModel){
        listData.add(mess)
        println("====>Api${listData}")
        notifyItemRangeInserted(listData.size-1,1)
    }

    fun appendFirst(mess:MessageModel)
    {
        listData.add(0,mess)
        println("====>Api${listData}")
        notifyItemInserted(0)
    }
}