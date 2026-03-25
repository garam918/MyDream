package com.garam.mydream.di

import com.garam.mydream.data.local.MyDreamDatabase
import com.garam.mydream.data.getDatabaseBuilder
import com.garam.mydream.data.local.getMyDreamDatabase
import com.garam.mydream.settings.AppSettingsStorage
import com.garam.mydream.settings.IosAppSettingsStorage
import org.koin.core.module.Module
import org.koin.dsl.module


actual fun platformModule(): Module = module {
    single<AppSettingsStorage> { IosAppSettingsStorage() }
    single<MyDreamDatabase> {
        val builder = getDatabaseBuilder()
        getMyDreamDatabase(builder)
    }
}
