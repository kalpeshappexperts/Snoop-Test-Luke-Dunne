package luke.com.snoop.beerapp.di

import android.content.Context
import android.content.SharedPreferences
import luke.com.snoop.beerapp.data.repositories.PunkRepositoryImpl
import luke.com.snoop.beerapp.data.service.PunkService
import luke.com.snoop.beerapp.domain.repositories.PunkRepository
import luke.com.snoop.beerapp.domain.useCases.GetBeerList
import luke.com.snoop.beerapp.domain.useCases.GetBeersById
import luke.com.snoop.beerapp.ui.utils.SharedPreferencesConfig
import luke.com.snoop.beerapp.ui.viewmodels.PunkViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoriesModule = module {
    single { PunkService() }
    single<PunkRepository> { PunkRepositoryImpl(get()) }
}


val viewModelModule = module {
    single { PunkViewModel(get(), get(), get()) }
}

val useCasesModule = module {
    single { GetBeersById(get()) }
    single { GetBeerList(get()) }

}

val sharedPreferences = module {

    single { SharedPreferencesConfig(androidContext())}
    single {
        getSharedPrefs(androidContext(), "com.example.android.PREFERENCE_FILE")
    }
    single<SharedPreferences.Editor> {
        getSharedPrefs(androidContext(), "com.example.android.PREFERENCE_FILE").edit()
    }
}

fun getSharedPrefs(context: Context, fileName: String): SharedPreferences {
    return context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
}