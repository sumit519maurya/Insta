package com.example.instagram

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class post_frag : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: adapter_pf_post_img
    private val postList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_frag, container, false)

        recyclerView = view.findViewById(R.id.postRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        adapter = adapter_pf_post_img(postList)
        recyclerView.adapter = adapter

        loadUserPosts()

        return view
    }

    private fun loadUserPosts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("Posts")
            .document(userId)
            .collection("UserPosts")
            .get()
            .addOnSuccessListener { result ->
                postList.clear()
                for (doc in result) {
                    val base64Post = doc.getString("post")
                    if (!base64Post.isNullOrEmpty()) {
                        postList.add(base64Post)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}
