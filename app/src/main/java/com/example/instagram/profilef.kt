package com.example.instagram

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class profilef : Fragment() {

    private lateinit var profileUsername: TextView
    private lateinit var profileImageView: ImageView
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profilef, container, false)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.profileToolbar)
        toolbar?.let {
            (activity as? AppCompatActivity)?.setSupportActionBar(it)
        }

        profileUsername = view.findViewById(R.id.profileUsername)
        profileImageView = view.findViewById(R.id.profileImageView)
        val addIcon: ImageView = view.findViewById(R.id.addText)
        val tabLayout = view.findViewById<TabLayout>(R.id.profileTabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.profileViewPager)
        viewPager.isUserInputEnabled = true
        viewPager.setNestedScrollingEnabled(false)


        val adapter = adapeter_profile(requireActivity()) // Use 'this' if in Activity, or 'requireActivity()' if in Fragment
        viewPager.adapter = adapter

        val tabTitles = arrayOf("Post", "Reel", "Tag")

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        addIcon.setOnClickListener {
            val intent = Intent(requireContext(), post_page::class.java)
            startActivity(intent)
        }

        profileImageView.setOnClickListener {
            openGallery()
        }

        val currentUser = auth.currentUser
        currentUser?.let {
            val userId = it.uid
            fetchUserName(userId)
            fetchProfileImage(userId)
        }

        return view
    }


    @SuppressLint("SetTextI18n")
    private fun fetchUserName(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userName = document.getString("name")
                    profileUsername.text = userName ?: "No name available"
                }
            }
            .addOnFailureListener {
                profileUsername.text = "Failed to load username"
            }
    }

    private fun fetchProfileImage(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val base64Image = document.getString("profileImage")
                base64Image?.let {
                    val imageBytes = Base64.decode(it, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    profileImageView.setImageBitmap(bitmap)
                }
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            imageUri?.let {
                val bitmap = getCompressedBitmap(it)
                profileImageView.setImageBitmap(bitmap)
                uploadProfileImage(bitmap)
            }
        }
    }

    private fun getCompressedBitmap(uri: Uri): Bitmap {
        val source = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.createSource(requireActivity().contentResolver, uri)
        } else {
            return MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        }
        val originalBitmap = ImageDecoder.decodeBitmap(source)
        val stream = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream)
        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun uploadProfileImage(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream)
        val byteArray = stream.toByteArray()
        val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .update("profileImage", base64Image)
            .addOnSuccessListener { /* Successfully saved */ }
            .addOnFailureListener { /* Handle error */ }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Handle settings
                true
            }
            R.id.action_logout -> {
                // Handle logout
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
