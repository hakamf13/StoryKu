package com.dicoding.intermediete.submissionstoryapps.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.dicoding.intermediete.submissionstoryapps.data.local.StoryModel
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.LoginResult
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository
import com.dicoding.intermediete.submissionstoryapps.ui.*
import com.dicoding.intermediete.submissionstoryapps.ui.story.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    companion object {
        private const val TOKEN = "token"
    }

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        mainViewModel = MainViewModel(storyRepository)
    }

    @Test
    fun `When Get User Account Should Not Null and Return Success`() {
        val dummyUser = DataDummy.generateDummyUserLoginResponse()
        val expectedUser = MutableLiveData<LoginResult>()
        expectedUser.value = dummyUser
        Mockito.`when`(storyRepository.getUserToken()).thenReturn(expectedUser.asFlow())

        val actualUser = mainViewModel.getUserToken().getOrAwaitValue()
        Mockito.verify(storyRepository).getUserToken()
        assertNotNull(actualUser)
        assertEquals(dummyUser, actualUser)
    }

    @Test
    fun `When Get Story Should Not Null and Return Success`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse().listStory
        val data: PagingData<StoryModel> = PagingDataTestSource.snapshot(dummyStory!!)
        val expectedStory = MutableLiveData<PagingData<StoryModel>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStories(TOKEN)).thenReturn(expectedStory)

        val actualStory: PagingData<StoryModel> = mainViewModel.getStories(TOKEN).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = NoopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot()[0]!!.name)
    }

    @Test
    fun `When User Logout Should Success`() = runTest {
        mainViewModel.userLogout()
        Mockito.verify(storyRepository).userLogout()
    }
}