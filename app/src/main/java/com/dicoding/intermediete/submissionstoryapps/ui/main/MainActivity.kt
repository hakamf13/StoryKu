package com.dicoding.intermediete.submissionstoryapps.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.intermediete.submissionstoryapps.R
import com.dicoding.intermediete.submissionstoryapps.ViewModelFactory
import com.dicoding.intermediete.submissionstoryapps.data.local.StoryModel
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.GetAllStoryResponse
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityMainBinding
import com.dicoding.intermediete.submissionstoryapps.ui.login.LoginActivity
import com.dicoding.intermediete.submissionstoryapps.ui.story.AddNewStoryActivity
import com.dicoding.intermediete.submissionstoryapps.ui.story.StoryAdapter
import com.dicoding.intermediete.submissionstoryapps.ui.story.StoryDetailActivity
import com.dicoding.intermediete.submissionstoryapps.ui.story.StoryDetailActivity.Companion.EXTRAS_STORY
import com.dicoding.intermediete.submissionstoryapps.ui.welcome.WelcomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        const val STORY_UPLOADED = "Story has been uploaded"
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var mainViewModel: MainViewModel

    private lateinit var storyAdapter: StoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
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

    private fun setupViewModel() {

        mainViewModel = ViewModelProvider(
            this@MainActivity,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUserToken().observe(
            this@MainActivity
        ) { session ->
            Log.d("", "Successfully to get Token")
            if (session.isLogin) {
                Log.d("TRUE", "Token has been found")
            } else {
                Log.e("FALSE", "Token not found")
                startActivity(
                    Intent(
                        this@MainActivity, WelcomeActivity::class.java
                    )
                )
                finish()
            }
        }

        mainViewModel.isLoading.observe(
            this@MainActivity
        ) { loader ->
            showLoading(loader)
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
        mainViewModel.getUserToken().observe(
            this@MainActivity
        ) {
            val token = "Bearer ${it.token}"
            val client = ApiConfig.getApiService().getStoryList(token)
            client.enqueue(object : Callback<GetAllStoryResponse> {

                override fun onResponse(
                    call: Call<GetAllStoryResponse>,
                    response: Response<GetAllStoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()!!
                        if (!responseBody.error) {
                            storyAdapter.storyList = responseBody.listStory
                            binding.rvStoryList.adapter = storyAdapter
                            Log.d("STORY", "Story has been fetched")
                        } else {
                            Log.e("STORY", "Story has not fetch")
                        }
                    }
                }

                override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
                    Log.e("STORY", "Story has not fetch")
                }
            })
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

                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}