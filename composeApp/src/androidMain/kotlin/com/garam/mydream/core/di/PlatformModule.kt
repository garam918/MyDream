package com.garam.mydream.core.di

import com.garam.mydream.core.database.MyDreamDatabase
import com.garam.mydream.core.database.getMyDreamDatabase
import com.garam.mydream.core.database.getDatabaseBuilder
import com.garam.mydream.core.platform.AndroidUrlLauncher
import com.garam.mydream.core.platform.UrlLauncher
import com.garam.mydream.core.settings.AndroidAppSettingsStorage
import com.garam.mydream.core.settings.AppSettingsStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppSettingsStorage> { AndroidAppSettingsStorage(get()) }
    single<UrlLauncher> { AndroidUrlLauncher(get()) }
    single<MyDreamDatabase> {
        val builder = getDatabaseBuilder(context = get())
        getMyDreamDatabase(builder)
    }
}
