package com.example.instagram

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.*

class post_page : AppCompatActivity() {

    private lateinit var selectedImageView: ImageView
    private lateinit var selectImageText: TextView
    private lateinit var captionInput: EditText
    private lateinit var postButton: TextView

    private val GALLERY_REQUEST_CODE = 101
    private var selectedImageUri: Uri? = null
    private var selectedBitmap: Bitmap? = null

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_post_page)

        selectedImageView = findViewById(R.id.selectedImage)
        selectImageText = findViewById(R.id.selectImageText)
        captionInput = findViewById(R.id.captionInput)
        postButton = findViewById(R.id.postButton)

        selectImageText.setOnClickListener {
            openGallery()
        }

        postButton.setOnClickListener {
            if (selectedBitmap != null && captionInput.text.isNotEmpty()) {
                uploadPost()
            } else {
                Toast.makeText(this, "Image and caption required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("IntentReset")
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let {
                val inputStream = contentResolver.openInputStream(it)
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)

                // Handle orientation
                val exif = androidx.exifinterface.media.ExifInterface(inputStream!!)
                val orientation = exif.getAttributeInt(
                    androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
                )

                val rotatedBitmap = when (orientation) {
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                    androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                    else -> bitmap
                }

                selectedBitmap = compressBitmap(rotatedBitmap)
                selectedImageView.setImageBitmap(selectedBitmap)
                selectedImageView.visibility = ImageView.VISIBLE
                selectImageText.visibility = TextView.GONE
            }
        }
    }


    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        // Resize to a smaller resolution (optional)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 600, 600, true)
        return scaledBitmap
    }

    private fun uploadPost() {
        val userId = auth.currentUser?.uid ?: return
        val caption = captionInput.text.toString()
        val postId = UUID.randomUUID().toString()

        val byteArrayOutputStream = ByteArrayOutputStream()
        selectedBitmap?.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream) // 30% quality
        val imageBytes = byteArrayOutputStream.toByteArray()
        val post = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        val postMap = hashMapOf(
            "caption" to caption,
            "post" to post,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("Posts")
            .document(userId)
            .collection("UserPosts")
            .document(postId)
            .set(postMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Post uploaded", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload post", Toast.LENGTH_SHORT).show()
            }
    }
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

}