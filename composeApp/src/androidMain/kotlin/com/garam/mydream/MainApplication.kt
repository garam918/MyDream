package com.garam.mydream

import android.app.Application
import com.garam.mydream.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        initKoin(
            appDeclaration = { androidContext(this@MainApplication) },
        )
    }

}