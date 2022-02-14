package com.example.snapeek

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class ChooseUserActivity : AppCompatActivity() {
    private var userListView: ListView?= null
    private var emails : ArrayList<String> = ArrayList()
    private var keys : ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)
        userListView = findViewById(R.id.userListView)
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        userListView?.adapter = adapter
        FirebaseDatabase.getInstance().reference.child("user").addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val email = snapshot.child("email").value as String
                emails.add(email)
                keys.add(snapshot.key.toString())
                adapter.notifyDataSetChanged()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
        userListView?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val snapMap : Map<String, String> = mapOf("from" to FirebaseAuth.getInstance().currentUser!!.email!!,"imageName" to intent.getStringExtra("imageName")!!, "imageURL" to intent.getStringExtra("imageURL")!!,"message" to intent.getStringExtra("message")!!)
            FirebaseDatabase.getInstance().reference.child("user").child(keys[position]).child("snaps").push().setValue(snapMap)
            val intent = Intent(this, SnapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}