package com.garam.mydream.di

import com.garam.mydream.data.local.MyDreamDatabase
import com.garam.mydream.data.getDatabaseBuilder
import com.garam.mydream.data.local.getMyDreamDatabase
import com.garam.mydream.settings.AndroidAppSettingsStorage
import com.garam.mydream.settings.AppSettingsStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppSettingsStorage> { AndroidAppSettingsStorage(get()) }
    single<MyDreamDatabase> {
        val builder = getDatabaseBuilder(context = get())
        getMyDreamDatabase(builder)
    }
}
