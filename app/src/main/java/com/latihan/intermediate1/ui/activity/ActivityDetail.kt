package com.latihan.intermediate1.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.latihan.intermediate1.databinding.ActivityDetailBinding
import com.latihan.intermediate1.utils.EXTRA_DESCRIPTION
import com.latihan.intermediate1.utils.EXTRA_IMAGE
import com.latihan.intermediate1.utils.STORY_NAME

class ActivityDetail : AppCompatActivity(){
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = "Detail"
        setDetail()
    }

    private fun setDetail() {
        val name = intent.getStringExtra(STORY_NAME)
        val desc = intent.getStringExtra(EXTRA_DESCRIPTION)
        val image = intent.getStringExtra(EXTRA_IMAGE)

        binding.apply {
            tvName.text = name
            tvDesc.text = desc
            Glide.with(this@ActivityDetail)
                .load(image)
                .into(ivStory)
        }
    }
}