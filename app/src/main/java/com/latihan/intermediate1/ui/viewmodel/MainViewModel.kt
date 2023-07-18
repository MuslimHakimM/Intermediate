package com.latihan.intermediate1.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.latihan.intermediate1.data.model.UserSession
import com.latihan.intermediate1.data.model.data.Repository
import com.latihan.intermediate1.data.model.stories.Story
import kotlinx.coroutines.launch

class MainViewModel(private val repo: Repository) : ViewModel() {
    fun getStories(): LiveData<PagingData<Story>> {
        return repo.getStory().cachedIn(viewModelScope)
    }

    fun getAllData(): LiveData<UserSession> {
        return repo.getUser()
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
        }
    }
}

