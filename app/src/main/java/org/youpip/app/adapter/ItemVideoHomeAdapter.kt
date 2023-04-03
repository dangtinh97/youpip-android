package org.youpip.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import org.youpip.app.R
import org.youpip.app.model.Video
import java.net.URL


class ItemVideoHomeAdapter(val callbackOnClick: (Video?) -> Unit):RecyclerView.Adapter<ItemVideoHomeAdapter.ViewHolder>() {
    private var listVideo = arrayListOf<Video>()
    private var width = 120;
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val tChanel = itemView.findViewById<TextView>(R.id.t_chanel)
        val tViewCount = itemView.findViewById<TextView>(R.id.t_count_view)
        val title = itemView.findViewById<TextView>(R.id.video_title)
        val thumbnail = itemView.findViewById<ShapeableImageView>(R.id.thumbnail)
        val time = itemView.findViewById<TextView>(R.id.video_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_video_big, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataVideo = listVideo[position]
        holder.thumbnail.setOnClickListener {
            callbackOnClick(dataVideo)
        }
        val layoutParams: ViewGroup.LayoutParams = holder.thumbnail.getLayoutParams()
        layoutParams.height = (width*9)/16;
        layoutParams.width = width;
        holder.thumbnail.setLayoutParams(layoutParams)

        Picasso.get()
            .load(dataVideo.thumbnail)
            .into(holder.thumbnail);
        holder.tChanel.setText(dataVideo.chanel_name)
        holder.tViewCount.setText(dataVideo.view_count_text)
        holder.title.setText(dataVideo.title)
        holder.time.setText(dataVideo.time_text)
    }

    override fun getItemCount(): Int {
        return listVideo.size
    }

    fun setData(list: ArrayList<Video>) {
        listVideo = list
        notifyDataSetChanged()
    }

    fun setWidth (widthSet:Int){
        width = widthSet
    }

    fun appendData(model:Video){
        listVideo.add(model)
        val pos = listVideo.size-1;
        notifyItemRangeInserted(pos,1)
    }
}