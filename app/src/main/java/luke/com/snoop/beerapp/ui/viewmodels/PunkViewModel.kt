package luke.com.snoop.beerapp.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import luke.com.snoop.beerapp.domain.entities.Beer
import luke.com.snoop.beerapp.domain.useCases.GetBeerList
import luke.com.snoop.beerapp.domain.useCases.GetBeersById
import luke.com.snoop.beerapp.domain.utils.Result
import luke.com.snoop.beerapp.ui.utils.Data
import luke.com.snoop.beerapp.ui.utils.SharedPreferencesConfig
import luke.com.snoop.beerapp.ui.utils.Status
import luke.com.snoop.beerapp.ui.viewmodels.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PunkViewModel(
    private val sharedPreferencesConfig: SharedPreferencesConfig,
    val getBeersById: GetBeersById,
    val getBeerList: GetBeerList
) : BaseViewModel() {

    private var mutableMainStateList = MutableLiveData<Data<List<Beer>>>()
    val mainStateList: LiveData<Data<List<Beer>>>
        get() {
            return mutableMainStateList
        }

    private var mutableMainStateDetail = MutableLiveData<Data<List<Beer>>>()
    val mainStateDetail: LiveData<Data<List<Beer>>>
        get() {
            return mutableMainStateDetail
        }

    fun onStartHome(page: Int, perPage: Int) = launch {
        mutableMainStateList.value = Data(responseType = Status.LOADING)
        when (val result = withContext(Dispatchers.IO) { getBeerList(page, perPage) }) {
            is Result.Failure -> {
                mutableMainStateList.value =
                    Data(responseType = Status.ERROR, error = result.exception)
            }
            is Result.Success -> {
                mutableMainStateList.value =
                    Data(responseType = Status.SUCCESSFUL, data = result.data)
            }
        }
    }

    fun onClickToBeerDetails(id: Int, context: Context) = launch {
        mutableMainStateDetail.value = Data(responseType = Status.LOADING)
        when (val result = withContext(Dispatchers.IO) { getBeersById(id) }) {
            is Result.Failure -> {
                mutableMainStateDetail.value =
                    Data(responseType = Status.ERROR, error = result.exception)
            }
            is Result.Success -> {
                mutableMainStateDetail.value =
                    Data(responseType = Status.SUCCESSFUL, data = result.data)
                result.data?.get(0)
                    ?.let { sharedPreferencesConfig.saveCurrentBeerData(it) }
            }
        }
    }

}