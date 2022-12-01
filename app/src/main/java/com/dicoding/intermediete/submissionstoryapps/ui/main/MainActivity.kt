package com.dicoding.intermediete.submissionstoryapps.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.dicoding.intermediete.submissionstoryapps.R
import com.dicoding.intermediete.submissionstoryapps.databinding.ActivityMainBinding
import com.dicoding.intermediete.submissionstoryapps.ui.login.LoginActivity
import com.dicoding.intermediete.submissionstoryapps.ui.welcome.WelcomeActivity
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.applyLinks

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = Link("")
            .setOnClickListener {
                startActivity(Intent(this, WelcomeActivity::class.java))
            }
        val testIntent = binding.testIntent
        testIntent.applyLinks(intent)
    }
}