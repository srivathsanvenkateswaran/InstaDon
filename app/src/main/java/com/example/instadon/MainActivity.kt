package com.example.instadon

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instadon.daos.PostDao
import com.example.instadon.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception


class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var adapter: PostAdapter
    private lateinit var postDao: PostDao
    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setSupportActionBar(toolbar)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        addNewPostFloatingActionButton.setOnClickListener{
            val intent = Intent(this, AddNewPostActivity::class.java)
            startActivity(intent)
        }

        setUpRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.googleSignOutMenu -> confirmGoogleSignOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun googleSignOut(){
        Toast.makeText(this, "Signing out...", Toast.LENGTH_LONG).show()

        auth = Firebase.auth
        auth.signOut()

        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    private fun confirmGoogleSignOut() {
//        Implement the Logic to sign out from Google here.

        val confirmSignOutDialogBox = AlertDialog.Builder(this)
                .setTitle("Confirm SignOut?")
                .setMessage("Do you want to Sign Out from this Account?")
                .setIcon(R.drawable.signout_alert_dialogue_icon)
                .setPositiveButton("Sign Out"){ _, _ ->
                    googleSignOut()
                }
                .setNegativeButton("Cancel"){ _, _ ->
                    Toast.makeText(this, "Sign Out denied...", Toast.LENGTH_LONG).show()
                }
                .create()
                .show()
    }

    private fun setUpRecyclerView() {
        postDao = PostDao()
        val postCollection = postDao.postCollection
        val query = postCollection.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        adapter = PostAdapter(recyclerViewOptions, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLiked(postID: String) {
        postDao.updateLikes(postID)
    }

    override fun onDeleted(postId: String) {
        GlobalScope.launch {
            auth = Firebase.auth
            val currentUserId = auth.currentUser!!.uid
            val post = postDao.getPostById(postId).await().toObject(Post::class.java)!!
            val createdById = post.createdBy.userId

            if(currentUserId == createdById){
                withContext(Dispatchers.Main){
                    val confirmDeleteDialog = android.app.AlertDialog.Builder(this@MainActivity)
                            .setTitle("Confirm Deleting This Post")
                            .setMessage("Do you want to delete this post?\nWarning: You can't undo this action..")
                            .setPositiveButton("Delete"){ _, _ ->
                                postDao.deletePost(postId)
                            }
                            .setNegativeButton("Cancel"){ _, _ ->
                                Toast.makeText(this@MainActivity, "Cancelled delete action", Toast.LENGTH_LONG).show()
                            }
                            .create()
                            .show()
                }
            }
        }
    }

    override fun editPost(postID: String) {
        val intent = Intent(this, EditPostActivity::class.java)
        intent.putExtra("postId", postID)
        startActivity(intent)
    }
}