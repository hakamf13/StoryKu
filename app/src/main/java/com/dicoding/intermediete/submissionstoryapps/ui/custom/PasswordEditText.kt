package com.dicoding.intermediete.submissionstoryapps.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PasswordEditText: AppCompatEditText {

    constructor(context: Context)
            : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet)
            : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int)
            : super(context, attributeSet, defStyleAttr) {
        init()
    }


    private fun init() {
        addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length < 6) {
                    error = "Min. 6 Characters!"
                }
            }
        })
    }
}