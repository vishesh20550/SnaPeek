package com.example.snapeek

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private var username: EditText?=null
    private var passWordEditText: EditText?=null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        username = findViewById(R.id.usernameEditText)
        passWordEditText = findViewById(R.id.passwordEditText)
        auth=Firebase.auth
        val currentUser = auth.currentUser
        if(currentUser != null){
            login()
        }
    }
    fun goClicked(view : View){
        auth.signInWithEmailAndPassword(username?.text.toString(), passWordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    login()
                } else {
                    auth.createUserWithEmailAndPassword(username?.text.toString(), passWordEditText?.text.toString())
                        .addOnCompleteListener(this){task1 ->
                            if(task1.isSuccessful){
                                task1.result.user?.let {
                                    FirebaseDatabase.getInstance().reference.child("user").child(
                                        it.uid).child("email").setValue(username?.text.toString())
                                }
                                login()
                            }
                            else{
                                Toast.makeText(this,"Login Failed. Try again later",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
    }
    private fun login(){
        //Login
        val intent = Intent(this,SnapsActivity::class.java)
        startActivity(intent)
    }
}