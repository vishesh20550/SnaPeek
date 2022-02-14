package com.example.snapeek

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class CreateSnapActivity : AppCompatActivity() {
    private var imageView: ImageView?=null
    private var chooseImageButton: Button?= null
    private var messageEditText: EditText?= null
    private var progressBar:ProgressBar?=null
    private var imageName=UUID.randomUUID().toString()+".jpg"
    private var imageUri: Uri?= null

    @RequiresApi(Build.VERSION_CODES.P)
    private var resultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode== Activity.RESULT_OK){
            val data: Intent?= result.data
            try{
                if (data != null) {
                    imageUri = data.data
                }
                imageView?.setImageURI(imageUri)
//                val source: ImageDecoder.Source? =
//                    data!!.data?.let { ImageDecoder.createSource(this.contentResolver, it) }
//                val bitmap: Bitmap? = source?.let { ImageDecoder.decodeBitmap(it) }
//                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data!!.data)
//                imageView?.setImageBitmap(bitmap)
            }catch(e:Exception){
                e.printStackTrace()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        imageView=findViewById(R.id.imageView)
        chooseImageButton= findViewById(R.id.chooseImageButton)
        messageEditText=findViewById(R.id.messageEditText)
        progressBar = findViewById(R.id.progressBar)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    fun nextClicked(view: View){
        progressBar?.visibility = View.VISIBLE
//        Log.i("Testing","Entered - 1")
//        // Get the data from an ImageView as bytes
//        imageView?.isDrawingCacheEnabled = true
//        imageView?.buildDrawingCache()
//        val bitmap = (imageView?.drawable as BitmapDrawable).bitmap
//        val baos = ByteArrayOutputStream()
//        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()
//        val uploadTask = FirebaseStorage.getInstance().reference.child("images").child(imageName).putBytes(data)
//        uploadTask.addOnFailureListener {
//            // Handle unsuccessful uploads
//            Toast.makeText(this,"Upload Failed :(",Toast.LENGTH_SHORT).show()
//        }.addOnSuccessListener {
//            var url: String
//            val task=it.storage.downloadUrl
//            task.addOnSuccessListener {
//                url=task.result.toString()
//                Log.i("DOWNLOAD URL",url)
//                Toast.makeText(this,"Upload Successful :)",Toast.LENGTH_SHORT).show()
//            }
//        }


        imageUri?.let { uri ->
            FirebaseStorage.getInstance().reference.child("images").child(imageName).putFile(
                uri
            ).addOnFailureListener {
                Toast.makeText(this,"Upload Failed :(",Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                var url: String
                val task=it.storage.downloadUrl
                task.addOnSuccessListener {
                    progressBar?.visibility=View.INVISIBLE
                    url=task.result.toString()
                    Log.i("DOWNLOAD URL",url)
                    Toast.makeText(this,"Upload Successful :)",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,ChooseUserActivity::class.java)
                    intent.putExtra("imageURL",url)
                    intent.putExtra("imageName",imageName)
                    intent.putExtra("message",messageEditText?.text.toString())
                    startActivity(intent)
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    fun chooseImageClicked(view: View){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getPhoto()
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        p1: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
        super.onRequestPermissionsResult(requestCode, p1, grantResults)
    }
}