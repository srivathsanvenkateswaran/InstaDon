package com.example.instadon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instadon.daos.PostDao
import com.example.instadon.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostAdapter(options: FirestoreRecyclerOptions<Post>, val listener: IPostAdapter) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
    options
) {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var postText: TextView = itemView.findViewById(R.id.postTitle)
        var userName: TextView = itemView.findViewById(R.id.userName)
        var userImage: ImageView = itemView.findViewById(R.id.userImage)
        var createdAt: TextView = itemView.findViewById(R.id.createdAt)
        var likeCount: TextView = itemView.findViewById(R.id.likeCount)
        var likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        var deleteButton: ImageView = itemView.findViewById(R.id.deletePostButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val postViewHolder = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))
        postViewHolder.likeButton.setOnClickListener{
            listener.onLiked(snapshots.getSnapshot(postViewHolder.adapterPosition).id)
        }
        postViewHolder.deleteButton.setOnClickListener{
            listener.onDeleted(snapshots.getSnapshot(postViewHolder.adapterPosition).id)
        }
        return postViewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.postText.text = model.postText
        holder.userName.text = model.createdBy.userDisplayName
        Glide.with(holder.userImage.context).load(model.createdBy.imageURL).circleCrop().into(holder.userImage)
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
        holder.likeCount.text = model.likedBy.size.toString()

        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currentUserId)

        if(isLiked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_baseline_favorite_24))
        }
        else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_baseline_favorite_border_24))
        }

        if(currentUserId == model.createdBy.userId){
            holder.deleteButton.setImageDrawable(ContextCompat.getDrawable(holder.deleteButton.context, R.drawable.ic_baseline_delete_24))
        }
        else{
            holder.deleteButton.visibility = View.GONE
        }
    }

}

interface IPostAdapter{
    fun onLiked(postID: String)
    fun onDeleted(postID: String)
}