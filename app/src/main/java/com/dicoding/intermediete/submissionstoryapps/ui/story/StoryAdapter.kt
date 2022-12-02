package com.dicoding.intermediete.submissionstoryapps.ui.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.ListStory
import com.dicoding.intermediete.submissionstoryapps.databinding.StoryListItemBinding

class StoryAdapter(private val storyList: ArrayList<ListStory>)
    : RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {

    companion object {
        const val EXTRA_STORY = "extra_story"
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    inner class ListViewHolder(var binding: StoryListItemBinding): RecyclerView.ViewHolder(binding.root) {

//        private var photo = binding.imageItemStory
//        private var

        fun bind(story: ListStory) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.imageItemStory)
            binding.apply {
                tvItemName.text = story.name
                tvItemDate.text = story.createdAt
                tvItemDescription.text = story.description
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                intent.putExtra(EXTRA_STORY, story)
                itemView.context.startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val storyBinding = StoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(storyBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val storyData = storyList[position]
        if (storyData != null) {
            holder.bind(storyData)
            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(storyData)
            }
        }
    }

    override fun getItemCount(): Int = storyList.size

//    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
//        this.onItemClickCallback = onItemClickCallback
//    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStory)
    }

}