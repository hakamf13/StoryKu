package com.dicoding.intermediete.submissionstoryapps.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.intermediete.submissionstoryapps.R
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityMainBinding
import com.dicoding.intermediete.submissionstoryapps.ui.login.LoginActivity
import com.dicoding.intermediete.submissionstoryapps.ui.story.AddNewStoryActivity
import com.dicoding.intermediete.submissionstoryapps.ui.story.StoryAdapter
import com.dicoding.intermediete.submissionstoryapps.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {

    companion object {
        const val STORY_UPLOADED = "Story has been uploaded"
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var storyAdapter: StoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupObserver()

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.title = "StoryKu"

        val layoutManager = LinearLayoutManager(this)
        binding.apply {
            rvStoryList.layoutManager = layoutManager
            rvStoryList.setHasFixedSize(true)
        }

        storyAdapter = StoryAdapter().apply {
            setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
                override fun onItemClicked(data: StoryModel) {
                    Intent(
                        this@MainActivity,
                        StoryDetailActivity::class.java
                    ).also {
                        it.putExtra(EXTRAS_STORY, data)
                        startActivity(it)
                    }
                }
            })
        }

    }

    private fun setupAction() {

        binding.swipeRefresh.setOnRefreshListener {
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
            binding.swipeRefresh.isRefreshing = false
        }

        binding.floatBtnAddStory.setOnClickListener {
            startActivity(
                Intent(
                this@MainActivity,
                AddNewStoryActivity::class.java
                )
            )
        }
    }

    private fun setupObserver() {

        mainViewModel.getUserToken().observe(this@MainActivity) { session ->
            if (session.isLogin) {
                getStoryList(session.token)
            } else {
                val intent = Intent(
                    this@MainActivity,
                    LoginActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.userLogout()

                val intent = Intent(
                    this@MainActivity,
                    LoginActivity::class.java
                )
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getStoryList(token: String) {
        showLoading(true)
        mainViewModel.getStories(token).observe(this@MainActivity) {
            showLoading(false)
            storyAdapter.storyList
            binding.rvStoryList.adapter = storyAdapter
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}