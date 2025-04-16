package com.example.instagram

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment

class Homef : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homef, container, false)

        val videoView = view.findViewById<VideoView>(R.id.feedVideo)
        val videoView1 = view.findViewById<VideoView>(R.id.feedVideo1)

        // First video (pinkiya)
        val videoUri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.pinkiya}")
        setupVideoView(videoView, videoUri)

        // Second video (dance)
        val videoUri1 = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.main1}")
        setupVideoView(videoView1, videoUri1)

        return view
    }

    private fun setupVideoView(videoView: VideoView, videoUri: Uri) {
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(videoUri)

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            val videoWidth = mp.videoWidth
            val videoHeight = mp.videoHeight
            val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()

            val screenWidth = resources.displayMetrics.widthPixels
            val layoutParams = videoView.layoutParams
            layoutParams.width = screenWidth
            layoutParams.height = (screenWidth / videoProportion).toInt()

            videoView.layoutParams = layoutParams
            videoView.start()
        }
    }
}
