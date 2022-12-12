package com.dicoding.intermediete.submissionstoryapps.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.LoginResult
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository
import com.dicoding.intermediete.submissionstoryapps.ui.DataDummy
import com.dicoding.intermediete.submissionstoryapps.ui.MainDispatcherRule
import com.dicoding.intermediete.submissionstoryapps.ui.getOrAwaitValue
import com.dicoding.intermediete.submissionstoryapps.utils.Result
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
class LoginViewModelTest{

    companion object {
        private const val EMAIL = "test@gmail.com"
        private const val PASSWORD = "password"
    }

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setup() {
        loginViewModel = LoginViewModel(storyRepository)
    }

    @Test
    fun `When User Login Should Not Null and Return Success`() {
        val dummyUser = DataDummy.generateDummyUserLoginResponse()
        val expectedUser = MutableLiveData<Result<LoginResult>>()
        expectedUser.value = Result.Success(dummyUser)
        Mockito.`when`(storyRepository.postLogin(EMAIL, PASSWORD)).thenReturn(expectedUser)

        val actualUser = loginViewModel.postLogin(EMAIL, PASSWORD).getOrAwaitValue()
        Mockito.verify(storyRepository).postLogin(EMAIL, PASSWORD)
        assertNotNull(actualUser)
        assertTrue(actualUser is Result.Success)
        assertEquals(dummyUser, (actualUser as Result.Success).data)
    }

    @Test
    fun `When User Success to Login Should Save User`() = runTest {
        val dummyUser = DataDummy.generateDummyUserLoginResponse()
        loginViewModel.userSave(dummyUser)
        Mockito.verify(storyRepository).userSave(dummyUser)
    }

}