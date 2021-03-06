package com.example.instagram_clone.navigation

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import bolts.Task
import com.example.instagram_clone.R
import com.example.instagram_clone.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    val PICK_IMAGE_FROM_ALBUM = 0

    var photoUri: Uri? = null

    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        // Firebase storage
        storage = FirebaseStorage.getInstance()
        // Firebase Database
        firestore = FirebaseFirestore.getInstance()
        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        add_photo_image.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

        addphoto_upload_btn.setOnClickListener {
            contentUpload()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            //????????? ?????????
            if(resultCode == Activity.RESULT_OK){
                //??????????????? ????????? ??????
                println(data?.data)
                photoUri = data?.data
                add_photo_image.setImageURI(data?.data)
            }
            else{
                finish()
            }

        }
    }

//    fun contentUpload(){
////        progress_bar.visibility = View.VISIBLE
//
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val imageFileName = "JPEG_" + timeStamp + "_.png"
//        val storageRef = storage?.reference?.child("images")?.child(imageFileName)
//        storageRef?.putFile(photoUri!!)?.addOnSuccessListener{ taskSnapshot ->
//            progress_bar.visibility = View.GONE
//
//            Toast.makeText(this, getString(R.string.upload_success),
//                Toast.LENGTH_SHORT).show()
//
//            val uri = taskSnapshot.downloadUrl
//            //????????? ????????? ??? ?????? ?????? ??? ?????????(?????????)??? ????????? ?????? ??????
//
//            //?????? ??????
//            val contentDTO = ContentDTO()
//
//            //????????? ??????
//            contentDTO.imageUrl = uri!!.toString()
//            //????????? UID
//            contentDTO.uid = auth?.currentUser?.uid
//            //???????????? ??????
//            contentDTO.explain = addphoto_edit_explain.text.toString()
//            //????????? ?????????
//            contentDTO.userId = auth?.currentUser?.email
//            //????????? ????????? ??????
//            contentDTO.timestamp = System.currentTimeMillis()
//
//            //???????????? ???????????? ?????? ??? ???????????? ??????
//            firestore?.collection("images")?.document()?.set(contentDTO)
//
//            setResult(Activity.RESULT_OK)
//            finish()
//        }
//            ?.addOnFailureListener { progress_bar.visibility = View.GONE
//
//                Toast.makeText(this, getString(R.string.upload_fail),
//                    Toast.LENGTH_SHORT).show()
//            }
//    }

    fun contentUpload() {
        //Make filename

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //Promise method
        storageRef?.putFile(photoUri!!)
            ?.continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }?.addOnSuccessListener { uri ->
            var contentDTO = ContentDTO()

            //Insert downloadUrl of image
            contentDTO.imageUrl = uri.toString()

            //Insert uid of user
            contentDTO.uid = auth?.currentUser?.uid

            //Insert userId
            contentDTO.userId = auth?.currentUser?.email

            //Insert explain of content
            contentDTO.explain = addphoto_edit_explain.text.toString()

            //Insert timestamp
            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)

            finish()
        }
    }

}