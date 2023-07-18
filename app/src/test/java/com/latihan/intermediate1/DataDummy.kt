package com.latihan.intermediate1

import com.latihan.intermediate1.data.model.stories.Story

object DataDummy {
    fun generateDummyStory(): List<Story> {
        val storyList = ArrayList<Story>()
        for (i in 0..10) {
            val story = Story(
                "Hakim",
                "coba",
                "user-092187317",
                "Hakim",
                "tidak berfoto.png",
                0.2,
                2.8
            )
            storyList.add(story)
        }
        return storyList
    }
}