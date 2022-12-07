package com.dicoding.intermediete.submissionstoryapps.ui.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.intermediete.submissionstoryapps.databinding.StoryListItemBinding

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    var storyList = mutableListOf<StoryModel>()

    class ListViewHolder(private val binding: StoryListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryModel) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.imageItemStory)
            binding.apply {
                tvItemName.text = story.name
                tvItemDate.text = story.createdAt
                tvItemDescription.text = story.description
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val storyBinding = StoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(storyBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(storyList[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback!!.onItemClicked(storyList[position])
        }
    }

    override fun getItemCount(): Int = storyList.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryModel)
    }

}
