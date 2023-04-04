package org.youpip.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import org.youpip.app.R
import org.youpip.app.databinding.ItemPostBinding
import org.youpip.app.model.PostModel

class DatingAdapter(val context:Context, var list:ArrayList<PostModel>,val callbackOnClick: (Array<String>) -> Unit):RecyclerView.Adapter<DatingAdapter.DatingViewHolder>() {
    inner class DatingViewHolder(val binding:ItemPostBinding): RecyclerView.ViewHolder(binding.root)

    private lateinit var btnFavorie:MaterialButton
    private lateinit var btnFavoried:MaterialButton
    private lateinit var btnComment:MaterialButton
    private lateinit var btnChat:MaterialButton

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatingViewHolder {
        return DatingViewHolder(ItemPostBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: DatingViewHolder, position: Int) {
        holder.binding.fullName.text = list[position].full_name
        btnFavorie = holder.binding.favorite
        btnFavoried = holder.binding.favorited
        btnComment = holder.binding.comment
        btnChat = holder.binding.chat
        if(list[position].liked){
            btnFavoried.visibility = ViewGroup.VISIBLE
            btnFavorie.visibility = ViewGroup.GONE
        }else{
            btnFavorie.visibility = ViewGroup.VISIBLE
            btnFavoried.visibility = ViewGroup.GONE
        }

        btnFavorie.setOnClickListener {
            holder.binding.favorited.visibility = ViewGroup.VISIBLE
            holder.binding.favorite.visibility = ViewGroup.GONE
            callbackOnClick(arrayOf<String>(list[position].post_oid,"LIKE"))
        }

        holder.binding.favorited.setOnClickListener {
            callbackOnClick(arrayOf<String>(list[position].post_oid,"DISLIKE"))
            holder.binding.favorited.visibility = ViewGroup.GONE
            holder.binding.favorite.visibility = ViewGroup.VISIBLE
        }



        btnComment.setOnClickListener {
            callbackOnClick(arrayOf<String>(list[position].post_oid,"COMMENT"))
        }

        btnChat.setOnClickListener {
            callbackOnClick(arrayOf(list[position].userOid.toString(),"CHAT"))
        }

        holder.binding.shortContent.text = list[position].content

        if(list[position].image!=""){
            Picasso.get()
                .load(list[position].image)
                .into(holder.binding.postImage);
        }else{
            holder.binding.postImage.setImageResource(R.drawable.gradation_black)
        }


        holder.binding.shortContent.setOnClickListener {
            val maxLine = holder.binding.shortContent.maxLines;
            if(maxLine>2){
                holder.binding.shortContent.maxLines = 2
            }else{
                holder.binding.shortContent.maxLines = Integer.MAX_VALUE
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun like(like:Boolean){
        if(like){
            btnFavoried.visibility = ViewGroup.VISIBLE
            btnFavorie.visibility = ViewGroup.GONE
        }else{
            btnFavorie.visibility = ViewGroup.VISIBLE
            btnFavoried.visibility = ViewGroup.GONE
        }
    }

    fun setData(data:PostModel){
        list.add(data)
        var size = list.size
        if(size==0){
            size = 1
        }
        notifyItemRangeInserted(size-1,1)
    }


}