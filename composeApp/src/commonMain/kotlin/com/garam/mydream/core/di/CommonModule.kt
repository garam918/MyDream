package com.garam.mydream.core.di

import com.garam.mydream.app.MainViewModel
import com.garam.mydream.core.auth.AuthRepository
import com.garam.mydream.core.auth.AuthRepositoryProvider
import com.garam.mydream.core.data.firebase.FirebaseDataSource
import com.garam.mydream.core.data.firebase.FirebaseDataSourceProvider
import com.garam.mydream.core.database.DreamAnalysisDao
import com.garam.mydream.core.database.MyDreamDatabase
import com.garam.mydream.core.database.TodayFortuneDao
import com.garam.mydream.core.database.UserDataDao
import com.garam.mydream.core.network.ApiService
import com.garam.mydream.core.data.repository.MainRepository
import com.garam.mydream.core.data.repository.MainRepositoryImpl
import com.garam.mydream.feature.calendar.CalendarViewModel
import com.garam.mydream.feature.dreamInterpretation.DreamInterpretationViewModel
import com.garam.mydream.feature.login.LoginViewModel
import com.garam.mydream.feature.record.RecordViewModel
import com.garam.mydream.feature.todayFortune.TodayFortuneViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun commonModule() : Module = module {

    single<UserDataDao> { get<MyDreamDatabase>().userDataDao() }
    single<DreamAnalysisDao> { get<MyDreamDatabase>().dreamAnalysisDao() }
    single<TodayFortuneDao> { get<MyDreamDatabase>().todayFortuneDao() }


    single<MainRepository> { get<MainRepositoryImpl>() }
    singleOf(::MainRepositoryImpl) { bind<MainRepository>() }

    single { HttpClient { install(ContentNegotiation) { json() } } }
    single { ApiService(get()) }

    single<FirebaseDataSource> { get<FirebaseDataSourceProvider>().get() }
    singleOf(::FirebaseDataSourceProvider)

    single<AuthRepository> { get<AuthRepositoryProvider>().get() }
    singleOf(::AuthRepositoryProvider)


    factory { MainViewModel(get()) }
    singleOf(::MainViewModel)

    factory { LoginViewModel(get(), get(), get(), get()) }
    singleOf(::LoginViewModel)

    factory { RecordViewModel(get(), get(), get(), get()) }
    singleOf(::RecordViewModel)

    factory { DreamInterpretationViewModel() }
    singleOf(::DreamInterpretationViewModel)

    factory { CalendarViewModel(get()) }
    singleOf(::CalendarViewModel)

    factory { TodayFortuneViewModel(get()) }
    singleOf(::TodayFortuneViewModel)

}
