package com.latihan.intermediate1.data.model.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.latihan.intermediate1.LoginPreference
import com.latihan.intermediate1.data.model.stories.Story
import com.latihan.intermediate1.data.remote.ApiService
import kotlinx.coroutines.flow.first

class SourcePaging(
    private val apiService: ApiService,
    private val pref: LoginPreference
) : PagingSource<Int, Story>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val token = "Bearer ${pref.getUser().first().token}"
            val responseData = apiService.getStories(token, page, params.loadSize).listStory
            LoadResult.Page(
                data = responseData,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (responseData.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}