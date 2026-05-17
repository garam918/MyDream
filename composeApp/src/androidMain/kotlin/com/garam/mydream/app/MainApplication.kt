package com.garam.mydream.app

import android.app.Application
import com.garam.mydream.core.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        initKoin(
            appDeclaration = { androidContext(this@MainApplication) },
        )
    }

}
