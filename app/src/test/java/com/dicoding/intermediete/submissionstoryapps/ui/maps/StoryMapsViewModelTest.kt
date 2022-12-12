package com.dicoding.intermediete.submissionstoryapps.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.dicoding.intermediete.submissionstoryapps.data.local.StoryModel
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.LoginResult
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository
import com.dicoding.intermediete.submissionstoryapps.ui.DataDummy
import com.dicoding.intermediete.submissionstoryapps.ui.MainDispatcherRule
import com.dicoding.intermediete.submissionstoryapps.utils.Result
import com.dicoding.intermediete.submissionstoryapps.ui.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class StoryMapsViewModelTest {

    companion object {
        private const val TOKEN = "token"
    }

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var storyMapsViewModel: StoryMapsViewModel

    @Before
    fun setup() {
        storyMapsViewModel = StoryMapsViewModel(storyRepository)
    }

    @Test
    fun `When Get User Account Should Not Null and Return Success`() {
        val dummyUser = DataDummy.generateDummyUserLoginResponse()
        val expectedUser = MutableLiveData<LoginResult>()
        expectedUser.value = dummyUser
        Mockito.`when`(storyRepository.getUserToken()).thenReturn(expectedUser.asFlow())

        val actualUser = storyMapsViewModel.getUserToken().getOrAwaitValue()
        Mockito.verify(storyRepository).getUserToken()
        assertNotNull(actualUser)
        assertEquals(dummyUser, actualUser)
    }

    @Test
    fun `When Get Story with Location Should Not Null and Return Success`() {
        val dummyStory = DataDummy.generateDummyStoryMapsResponse()
        val expectedStory = MutableLiveData<Result<List<StoryModel>>>()
        expectedStory.value = Result.Success(dummyStory.listStory!!)
        Mockito.`when`(storyRepository.getStoriesWithLocation(TOKEN)).thenReturn(expectedStory)

        val actualStory = storyMapsViewModel.getStoriesWithLocation(TOKEN).getOrAwaitValue()
        Mockito.verify(storyRepository).getStoriesWithLocation(TOKEN)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
        assertEquals(dummyStory.listStory!!.size, (actualStory as Result.Success).data.size)
        assertEquals(dummyStory.listStory!![0].id, actualStory.data[0].id)
    }

}