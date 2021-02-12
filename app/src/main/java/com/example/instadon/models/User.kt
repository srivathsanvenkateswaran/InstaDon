package com.example.instadon.models

data class User(
        val userId: String = "",
        val userDisplayName: String? = "",
        val imageURL: String = ""
)