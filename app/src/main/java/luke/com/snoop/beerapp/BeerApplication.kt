package luke.com.snoop.beerapp

import android.app.Application
import luke.com.snoop.beerapp.di.*
import com.facebook.drawee.backends.pipeline.Fresco
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BeerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        startKoin {
            androidContext(this@BeerApplication)
            modules(listOf(repositoriesModule, viewModelModule,useCasesModule, sharedPreferences))
        }
    }
}