package luke.com.snoop.beerapp.ui.viewmodels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import luke.com.snoop.beerapp.di.sharedPreferences
import luke.com.snoop.beerapp.di.useCasesModule
import luke.com.snoop.beerapp.domain.entities.Beer
import luke.com.snoop.beerapp.domain.useCases.GetBeerList
import luke.com.snoop.beerapp.domain.useCases.GetBeersById
import luke.com.snoop.beerapp.domain.utils.Result
import luke.com.snoop.beerapp.ui.utils.Data
import luke.com.snoop.beerapp.ui.utils.SharedPreferencesConfig
import luke.com.snoop.beerapp.ui.utils.Status
import com.google.common.truth.Truth
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import luke.com.snoop.beerapp.di.viewModelModule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


private const val VALID_ID = 33
private const val INVALID_ID = -1
private const val PAGE_VALID = 1
private const val PER_PAGE_VALID = 80
private const val PER_PAGE_INVALID = 100

class PunkViewModelTest : AutoCloseKoinTest() {

    @ObsoleteCoroutinesApi
    private var mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var subject: PunkViewModel

    @Mock
    lateinit var beerValidResult: Result.Success<List<Beer>>

    @Mock
    lateinit var beerInvalidResult: Result.Failure

    @Mock
    lateinit var beers: List<Beer>

    @Mock
    lateinit var exception: Exception

    private val getBeerListUseCase: GetBeerList by inject()
    private val getBeersByIdUseCase: GetBeersById by inject()
    private val sharedPreferencesConfig: SharedPreferencesConfig by inject()
    private val context: Context by inject()


    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
        startKoin {
            androidContext(luke.com.snoop.beerapp.BeerApplication())
            modules(
                listOf(
                    viewModelModule,useCasesModule, sharedPreferences
                )
            )
        }

        declareMock<GetBeerList>()
        declareMock<GetBeersById>()
        MockitoAnnotations.initMocks(this)
        subject = PunkViewModel(
            sharedPreferencesConfig,
            getBeersByIdUseCase,
            getBeerListUseCase
        )
    }

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    @After
    fun after() {
        stopKoin()
        mainThreadSurrogate.close()
        Dispatchers.resetMain()
    }
    @Test
    fun `when getBeerList is successful`() {
        Mockito.`when`(getBeerListUseCase.invoke(PAGE_VALID, PER_PAGE_VALID)).thenReturn(beerValidResult)
        Mockito.`when`(beerValidResult.data).thenReturn(beers)
        runBlocking {
            subject.onStartHome(PAGE_VALID, PER_PAGE_VALID).join()
        }
        val liveDataUnderTest = subject.mainStateList.testObserver()
        Truth.assertThat(liveDataUnderTest.observedValues[0])
            .isEqualTo(Data(responseType = Status.SUCCESSFUL, data = beerValidResult.data))
    }

    @Test
    fun `when getBeerList is failure`() {
        Mockito.`when`(getBeerListUseCase.invoke(PAGE_VALID, PER_PAGE_INVALID)).thenReturn(beerInvalidResult)
        Mockito.`when`(beerInvalidResult.exception).thenReturn(exception)
        runBlocking {
            subject.onStartHome(PAGE_VALID, PER_PAGE_INVALID).join()
        }
        val liveDataUnderTest = subject.mainStateList.testObserver()
        Truth.assertThat(liveDataUnderTest.observedValues[0])
            .isEqualTo(Data(responseType = Status.ERROR, data = null, error = beerInvalidResult.exception))
    }

    @Test
    fun `when getBeersById is successful`() {
        Mockito.`when`(getBeersByIdUseCase.invoke(VALID_ID)).thenReturn(beerValidResult)
        Mockito.`when`(beerValidResult.data).thenReturn(beers)
        runBlocking {
            subject.onClickToBeerDetails(VALID_ID, context).join()
        }
        val liveDataUnderTest = subject.mainStateDetail.testObserver()
        Truth.assertThat(liveDataUnderTest.observedValues[0])
            .isEqualTo(Data(responseType = Status.SUCCESSFUL, data = beerValidResult.data))
    }

    @Test
    fun `when getBeersById is failure`() {
        Mockito.`when`(getBeersByIdUseCase.invoke(INVALID_ID)).thenReturn(beerInvalidResult)
        Mockito.`when`(beerInvalidResult.exception).thenReturn(exception)
        runBlocking {
            subject.onClickToBeerDetails(INVALID_ID, context).join()
        }
        val liveDataUnderTest = subject.mainStateDetail.testObserver()
        Truth.assertThat(liveDataUnderTest.observedValues[0])
            .isEqualTo(Data(responseType = Status.ERROR, data = null, error = beerInvalidResult.exception))
    }

    class TestObserver<T> : Observer<T> {
        val observedValues = mutableListOf<T?>()
        override fun onChanged(value: T?) {
            observedValues.add(value)
        }
    }
    private fun <T> LiveData<T>.testObserver() = TestObserver<T>().also {
        observeForever(it)
    }
}