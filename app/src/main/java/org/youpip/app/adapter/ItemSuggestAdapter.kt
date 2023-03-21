package org.youpip.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.NonDisposableHandle.parent
import org.youpip.app.R
import org.youpip.app.model.Video

class ItemSuggestAdapter(val callbackOnClick: (String) -> Unit):RecyclerView.Adapter<ItemSuggestAdapter.ViewHolder>() {

    private var list = emptyList<String>()

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val text = itemView.findViewById<TextView>(R.id.t_suggest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_suggest_text, parent, false)
        return ItemSuggestAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.setText(list[position])
        holder.text.setOnClickListener {
            callbackOnClick(list[position])
        }
    }

    fun setData(data:List<String>){
        list = data
        println("====>${list}")
        notifyDataSetChanged()
    }
}