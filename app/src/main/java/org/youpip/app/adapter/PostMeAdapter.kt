package org.youpip.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.youpip.app.R

class PostMeAdapter(val callbackOnClick: (String) -> Unit): RecyclerView.Adapter<PostMeAdapter.ViewHolder>() {
    private var listData = emptyList<String>()
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_post_me, parent, false)
        return PostMeAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun setData(data:List<String>){
        listData = data
        notifyDataSetChanged()
    }
}