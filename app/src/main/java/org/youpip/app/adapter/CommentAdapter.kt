package org.youpip.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.youpip.app.R
import org.youpip.app.databinding.ItemCommentBinding
import org.youpip.app.model.CommentModel

class CommentAdapter(val callbackOnClick: (String) -> Unit): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private var listData = emptyList<CommentModel>()
    private lateinit var binding:ItemCommentBinding

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.full_name)
        val content = itemView.findViewById<TextView>(R.id.content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = listData[position].fullName
        holder.content.text = listData[position].content
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun setData(data:List<CommentModel>){
        listData = data
        notifyDataSetChanged()
    }

}