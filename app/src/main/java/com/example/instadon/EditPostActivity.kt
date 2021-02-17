package com.example.instadon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.instadon.daos.PostDao
import com.example.instadon.models.Post
import kotlinx.android.synthetic.main.activity_edit_post.*
import kotlinx.android.synthetic.main.item_post.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class EditPostActivity : AppCompatActivity() {

    private val postDao = PostDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        var postText = ""
        val postId = intent.getStringExtra("postId")!!
        CoroutineScope(Dispatchers.IO).launch {
            var post = postDao.getPostById(postId).await().toObject(Post::class.java)
            postText = post?.postText.toString()
            withContext(Dispatchers.Main){
                postTextEditText.setText(postText)
            }
        }

        updateTextButton.setOnClickListener{
            val newPostText = postTextEditText.text.toString().trim()
            if(newPostText.isNotEmpty()){
                postDao.updatePost(postId, newPostText)
                finish()
            }
        }
    }
}