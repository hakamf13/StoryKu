package com.dicoding.intermediete.submissionstoryapps.ui.story

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityStoryDetailBinding
import com.dicoding.intermediete.submissionstoryapps.utils.ViewModelFactory
import com.dicoding.intermediete.submissionstoryapps.utils.Result

@Suppress("DEPRECATION")
class StoryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRAS_STORY = "extras_story"
    }

    private val binding: ActivityStoryDetailBinding by lazy {
        ActivityStoryDetailBinding.inflate(layoutInflater)
    }

    private val storyDetailViewModel: StoryDetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    val story = intent.getStringExtra(EXTRAS_STORY).toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupAction(story)
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

    private fun setupAction(id: String) {
        storyDetailViewModel.getDetailStories(id).observe(
            this@StoryDetailActivity
        ) {
            if (it != null) {
                when (it) {

                    is Result.Loading -> {

                    }

                    is Result.Success -> {
                        Glide.with(applicationContext)
                            .load(it.data.photoUrl)
                            .into(binding.imageItemStory)
                        binding.apply {
                            tvItemName.text = it.data.name
                            tvItemDescription.text = it.data.description
                            tvItemDate.text = it.data.createdAt
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@StoryDetailActivity,
                            it.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
    }
}
