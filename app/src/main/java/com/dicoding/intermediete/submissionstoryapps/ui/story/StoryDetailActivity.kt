package com.dicoding.intermediete.submissionstoryapps.ui.story

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.dicoding.intermediete.submissionstoryapps.data.local.StoryModel
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityStoryDetailBinding

@Suppress("DEPRECATION")
class StoryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRAS_STORY = "extras_story"
    }

    private val binding: ActivityStoryDetailBinding by lazy {
        ActivityStoryDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupObserver()
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupObserver() {
        val story = intent?.extras?.getParcelable<StoryModel>(EXTRAS_STORY)
        Glide.with(applicationContext)
            .load(story?.photoUrl)
            .into(binding.imageItemStory)
        binding.apply {
            tvItemName.text = story?.name
            tvItemDescription.text = story?.description
            tvItemDate.text = story?.createdAt
        }
    }
}
