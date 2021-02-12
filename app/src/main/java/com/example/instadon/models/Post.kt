package com.example.instadon.models

data class Post(
    val postText: String = "",
    val createdBy: User = User(),
    val createdAt: Long = 0L,
    val likedBy: ArrayList<String> = ArrayList()
)