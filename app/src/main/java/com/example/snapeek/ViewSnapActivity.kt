package com.example.snapeek

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ViewSnapActivity : AppCompatActivity() {
    private var snapImageView : ImageView? =null
    private var messageTextView : TextView?= null
    var progressBar: ProgressBar?=null
    private lateinit var mAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)
        progressBar = findViewById(R.id.progressBar2)
        progressBar?.visibility = View.VISIBLE
        mAuth = Firebase.auth
        snapImageView = findViewById(R.id.snapImageView)
        messageTextView = findViewById(R.id.messageTextView)
        messageTextView?.text = intent.getStringExtra("message")
        Glide.with(this).load(intent.getStringExtra("imageURL")).listener(object :
            RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                progressBar?.visibility =View.GONE
                return false
            }
            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                progressBar?.visibility =View.GONE
                return false
            }
        }).into(snapImageView!!)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseDatabase.getInstance().reference.child("user").child(mAuth.currentUser?.uid.toString()).child("snaps").child(intent.getStringExtra("snapKey").toString()).removeValue()
        FirebaseStorage.getInstance().reference.child("images").child(intent.getStringExtra("imageName").toString()).delete()
    }
}