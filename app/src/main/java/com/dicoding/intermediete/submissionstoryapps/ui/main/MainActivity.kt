package com.dicoding.intermediete.submissionstoryapps.ui.main

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.intermediete.submissionstoryapps.R
import com.dicoding.intermediete.submissionstoryapps.ViewModelFactory
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.ListStory
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityMainBinding
import com.dicoding.intermediete.submissionstoryapps.ui.login.LoginActivity
import com.dicoding.intermediete.submissionstoryapps.ui.story.AddNewStoryActivity
import com.dicoding.intermediete.submissionstoryapps.ui.story.StoryAdapter
import com.dicoding.intermediete.submissionstoryapps.ui.welcome.WelcomeActivity
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.applyLinks

class MainActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var mainViewModel: MainViewModel

    private val storyListAdapter by lazy { StoryAdapter(storyList) }

    private val storyList = ArrayList<ListStory>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()

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
            rvStoryList.adapter = storyListAdapter
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this@MainActivity,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.storyList.observe(this@MainActivity) {
            storyList.clear()
            for (story in it) {
                storyList.add(
                    ListStory(
                        story.id,
                        story.name,
                        story.description,
                        story.photoUrl,
                        story.createdAt
                    )
                )
            }
        }

        mainViewModel.isLoading.observe(
            this@MainActivity
        ) { loader ->
            showLoading(loader)
        }

        mainViewModel.getUserToken().observe(
            this@MainActivity
        ) { state ->
            Log.d("", "Successfully to get Token")
            if (state.login) {
                mainViewModel.setUserData(state.token)
                Log.e("getUserToken", "berhasil mengambil token")
            } else {
                startActivity(
                    Intent(
                    this@MainActivity, WelcomeActivity::class.java
                    )
                )
                Log.e("getUserToken", "gagal mengambil token")
                finish()
            }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_logout -> {
                /*AlertDialog.Builder(
                    this@MainActivity).apply {
                        setTitle("")
                        setMessage("")
                        setPositiveButton("") { _, _ ->
                            mainViewModel.userLogout()
                        }
                        setNegativeButton("") { _, _ ->
                            AlertDialog.Builder(
                                this@MainActivity
                            ).create().cancel()
                        }
                    create()
                    show()
                }*/
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