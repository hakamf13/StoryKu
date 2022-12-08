package com.dicoding.intermediete.submissionstoryapps.ui

import androidx.recyclerview.widget.ListUpdateCallback

object NoopListUpdateCallback: ListUpdateCallback {

    override fun onInserted(position: Int, count: Int) {

    }

    override fun onRemoved(position: Int, count: Int) {

    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {

    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {

    }
}