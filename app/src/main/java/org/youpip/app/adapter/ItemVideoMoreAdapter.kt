package org.youpip.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import org.youpip.app.R
import org.youpip.app.model.Video

class ItemVideoMoreAdapter(val callbackOnClick: (Video) -> Unit): RecyclerView.Adapter<ItemVideoMoreAdapter.ViewHolder>() {
    private var listVideo = emptyList<Video>()
    private var width = 120;
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tChanel = itemView.findViewById<TextView>(R.id.t_chanel)
        val title = itemView.findViewById<TextView>(R.id.video_title)
        val thumbnail = itemView.findViewById<ShapeableImageView>(R.id.thumbnail)
        val layout = itemView.findViewById<LinearLayout>(R.id.layout_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_video_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataVideo = listVideo[position]
        holder.layout.setOnClickListener {
            callbackOnClick(dataVideo)
        }
        Picasso.get()
            .load(dataVideo.thumbnail)
            .into(holder.thumbnail);
        holder.tChanel.text = dataVideo.chanel_name+" | "+dataVideo.time_text
        holder.title.setText(dataVideo.title)
    }

    override fun getItemCount(): Int {
        return listVideo.size
    }

    fun setData(list: List<Video>) {
        listVideo = list
        notifyDataSetChanged()
    }

    fun setWidth (widthSet:Int){
        width = widthSet
    }
}