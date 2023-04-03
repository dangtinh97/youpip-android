package org.youpip.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import org.youpip.app.R
import org.youpip.app.model.PostModel

class PostMeAdapter(val callbackOnClick: (Array<String>) -> Unit): RecyclerView.Adapter<PostMeAdapter.ViewHolder>() {
    private var listData = arrayListOf<PostModel>()
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val content:TextView = itemView.findViewById(R.id.content)
        val image:ImageView = itemView.findViewById(R.id.image)
        val trash:ImageView = itemView.findViewById(R.id.action)
        val btnComment:MaterialButton = itemView.findViewById(R.id.comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_post_me, parent, false)
        return PostMeAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.content.text = listData[position].content
        Picasso.get()
            .load(listData[position].image)
            .into(holder.image)

        holder.trash.setOnClickListener {
            callbackOnClick(arrayOf(listData[position].post_oid,"DELETE"))
        }

        holder.btnComment.setOnClickListener {
            callbackOnClick(arrayOf(listData[position].post_oid,"COMMENT"))
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun setData(data:ArrayList<PostModel>){
        listData = data
        notifyDataSetChanged()
    }

    fun appendData(item:PostModel){
        listData.add(item)
        notifyItemRangeInserted(listData.size-1,1)
    }
}