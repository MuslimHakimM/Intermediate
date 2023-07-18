package com.latihan.intermediate1.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.latihan.intermediate1.DataDummy
import com.latihan.intermediate1.MainDispatcherRule
import com.latihan.intermediate1.data.model.adapter.ListAdapter
import com.latihan.intermediate1.data.model.data.Repository
import com.latihan.intermediate1.data.model.stories.Story
import com.latihan.intermediate1.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{

    private lateinit var mainViewModel: MainViewModel
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    @Mock
    private val repository = Mockito.mock(Repository::class.java)

    @Before
    fun getViewModel() {
        mainViewModel = MainViewModel(repository)
    }
    @Test
    fun `when Get Story is Not Null `() = runTest {
        val dummyStory = DataDummy.generateDummyStory()
        val data: PagingData<Story> = PagingSourceTest.snapshot(dummyStory)
        val storiesExpect = MutableLiveData<PagingData<Story>>()

        storiesExpect.value = data
        Mockito.`when`(repository.getStory()).thenReturn(storiesExpect)

        val actualStory: PagingData<Story> =
            mainViewModel.getStories().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListAdapter.DIFF_CALLBACK,
            updateCallback = listUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }
    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val storiesExpect = MutableLiveData<PagingData<Story>>()
        storiesExpect.value = data
        Mockito.`when`(repository.getStory()).thenReturn(storiesExpect)
        val trueStory: PagingData<Story> = mainViewModel.getStories().getOrAwaitValue()
        val difference = AsyncPagingDataDiffer(
            diffCallback = ListAdapter.DIFF_CALLBACK,
            updateCallback = listUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        difference.submitData(trueStory)
        Assert.assertEquals(0, difference.snapshot().size)
    }

    private val listUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}

class PagingSourceTest: PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}
