package com.example.instadon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.instadon.daos.PostDao
import kotlinx.android.synthetic.main.activity_add_new_post.*

class AddNewPostActivity : AppCompatActivity() {

    private lateinit var postDao: PostDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_post)

        addNewPostButton.setOnClickListener{
            val postText = postTextEditText.text.toString().trim()

            if(postText.isNotEmpty()){
//                Create a New Post
                postDao = PostDao()
                postDao.addPost(postText)
                finish()
            }
        }
    }
}