package com.garam.mydream.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration


fun initKoin(appDeclaration: KoinAppDeclaration = {}) {

    startKoin {
        appDeclaration()
        modules(
            commonModule() + platformModule()
        )

    }

}

fun initKoinIos() = initKoin(appDeclaration = {})