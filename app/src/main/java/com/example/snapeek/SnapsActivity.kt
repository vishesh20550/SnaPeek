package com.example.snapeek

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SnapsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var snapsListView : ListView?= null
    private var emails : ArrayList<String> = ArrayList()
    private var snapshots : ArrayList<DataSnapshot> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)
        auth= Firebase.auth
        snapsListView = findViewById(R.id.snapsListView)
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        snapsListView?.adapter = adapter
        FirebaseDatabase.getInstance().reference.child("user").child(auth.currentUser?.uid.toString()).child("snaps").addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                emails.add(snapshot.child("from").value as String)
                snapshots.add(snapshot)
                adapter.notifyDataSetChanged()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                for(i in 0 until emails.size){
                    if(snapshot.key == snapshots[i].key){
                        emails.removeAt(i)
                        snapshots.removeAt(i)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val snapshot = snapshots[position]
            val intent = Intent(this,ViewSnapActivity::class.java)
            intent.putExtra("message",snapshot.child("message").value as String)
            intent.putExtra("imageURL",snapshot.child("imageURL").value as String)
            intent.putExtra("imageName",snapshot.child("imageName").value as String)
            intent.putExtra("snapKey",snapshot.key)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.snaps,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId ==R.id.logout){
            auth.signOut()
            finish()
        }else if(item.itemId == R.id.createSnap){
            val intent = Intent(this, CreateSnapActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
//        super.onBackPressed();
        val back = AlertDialog.Builder(this)
        back.setTitle("Logout")
        back.setMessage("Are you sure ?")
        back.setPositiveButton("Yes"){ _, _ ->
            auth.signOut()
            finish()
        }
        back.setNegativeButton("Cancel"){_,_ ->

        }
        val alertDialog:AlertDialog= back.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}