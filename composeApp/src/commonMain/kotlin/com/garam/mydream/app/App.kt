package com.garam.mydream.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.lexilabs.basic.ads.BasicAds
import app.lexilabs.basic.ads.DependsOnGoogleMobileAds
import com.garam.mydream.core.network.DreamResponse
import com.garam.mydream.core.localization.AppLanguage
import com.garam.mydream.core.localization.AppLanguageProvider
import com.garam.mydream.core.designsystem.DarkColorPalette
import com.garam.mydream.core.designsystem.LightColorPalette
import com.garam.mydream.core.designsystem.LocalMyColors
import com.garam.mydream.core.designsystem.MyTheme
import com.garam.mydream.core.designsystem.fontFamily
import com.garam.mydream.core.settings.AppSettingsStorage
import com.garam.mydream.feature.login.LoginBottomSheetScreen
import com.garam.mydream.feature.calendar.DreamCalendar
import com.garam.mydream.feature.dreamInterpretation.DreamInterpretation
import com.garam.mydream.feature.onboarding.OnboardingScreen
import com.garam.mydream.feature.record.DreamRecord
import com.garam.mydream.feature.setting.AccountManagementScreen
import com.garam.mydream.feature.setting.AppThemeMode
import com.garam.mydream.feature.setting.LanguageSettingScreen
import com.garam.mydream.feature.setting.NotificationTimeSettingScreen
import com.garam.mydream.feature.setting.SettingScreen
import com.garam.mydream.feature.setting.SubscriptionManagementScreen
import com.garam.mydream.feature.setting.ThemeSettingScreen
import com.garam.mydream.feature.todayFortune.TodayFortuneScreen
import mydream.composeapp.generated.resources.Res
import mydream.composeapp.generated.resources.bottom_menu_dream_calendar_title_text
import mydream.composeapp.generated.resources.bottom_menu_insight_title_text
import mydream.composeapp.generated.resources.bottom_menu_my_info_title_text
import mydream.composeapp.generated.resources.bottom_menu_today_dream_title_text
import mydream.composeapp.generated.resources.bottom_menu_today_tarot_title_text
import mydream.composeapp.generated.resources.setting_screen_title
import mydream.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val DREAM_INTERPRETATION = "dreamInterpretation"
    const val SETTINGS = "settings"
    const val ACCOUNT_MANAGEMENT = "accountManagement"
    const val LANGUAGE_SETTING = "languageSetting"
    const val THEME_SETTING = "themeSetting"
    const val NOTIFICATION_TIME_SETTING = "notificationTimeSetting"
    const val SUBSCRIPTION_MANAGEMENT = "subscriptionManagement"
}

@OptIn(DependsOnGoogleMobileAds::class)
@Composable
fun App(mainViewModel: MainViewModel = koinViewModel()) {

    BasicAds.Initialize()

    val appSettingsStorage = koinInject<AppSettingsStorage>()
    val initialLanguageName = remember(appSettingsStorage) {
        enumValueOrDefault<AppLanguage>(
            value = appSettingsStorage.getLanguageName(),
            defaultValue = appSettingsStorage.getSystemLanguageName()
        )
    }
    val initialThemeModeName = remember(appSettingsStorage) {
        enumValueOrDefault<AppThemeMode>(
            value = appSettingsStorage.getThemeModeName(),
            defaultValue = AppThemeMode.LIGHT.name
        )
    }

    var selectedLanguageName by rememberSaveable { mutableStateOf(initialLanguageName) }
    var selectedThemeModeName by rememberSaveable { mutableStateOf(initialThemeModeName) }
    val selectedLanguage = AppLanguage.valueOf(selectedLanguageName)
    val selectedThemeMode = AppThemeMode.valueOf(selectedThemeModeName)

    val currentUser = mainViewModel.currentUser.collectAsState()

    val isLoggedIn = mainViewModel.getLoggedIn()

    val navController = rememberNavController()
    var latestDreamResponse by remember { mutableStateOf<DreamResponse?>(null) }
    var showLoginBottomSheet by remember { mutableStateOf(false) }
//    val repo = AuthRepositoryProvider().get()

    LaunchedEffect(currentUser.value) {

//        val isActiveSubscription = revenueCatRepo.hasActiveEntitlement()
//
//
//        currentUser.value?.let { user ->
//            updateUserScope.launch {
//                mainViewModel.updateUser(user.copy(paid = isActiveSubscription))
//            }
//        }
    }

    AppLanguageProvider(appLanguage = selectedLanguage) {
        MyCustomTheme(isDarkTheme = selectedThemeMode == AppThemeMode.DARK) {
            if (showLoginBottomSheet) {
            LoginBottomSheetScreen(
                onDismiss = {
                    showLoginBottomSheet = false
                },
                onSuccessLogin = {
                        showLoginBottomSheet = false
                        mainViewModel.refreshCurrentUser()
                        navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SETTINGS) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                showGuestContinueButton = false
            )
        }

            val startDestination = if (isLoggedIn) Routes.HOME else Routes.ONBOARDING

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
            // 온보딩 (로그인) 화면
            composable(Routes.ONBOARDING) {
                OnboardingScreen(onLoginSuccess = {
                    mainViewModel.refreshCurrentUser()
                    // 로그인 성공 시 홈으로 이동하며 백스택 비우기
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                })
            }

            // 메인 홈 화면 (기존에 작성하신 코드)
            composable(Routes.HOME) {
                HomeBottomNavigation(
                    onNavigateToDreamInterpretation = { dreamResponse ->
                        latestDreamResponse = dreamResponse
                        navController.navigate(Routes.DREAM_INTERPRETATION)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Routes.SETTINGS)
                    }
                )
            }

            composable(Routes.DREAM_INTERPRETATION) {
                latestDreamResponse?.let { dreamResponse ->
                    DreamInterpretation(
                        dreamResponse = dreamResponse,
                        onBackPressed = {
                            navController.popBackStack()
                        }
                    )
                } ?: LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }

            composable(Routes.SETTINGS) {
                SettingScreen(
                    email = currentUser.value?.email,
                    selectedLanguage = selectedLanguage,
                    selectedThemeMode = selectedThemeMode,
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onNavigateToAccountManagement = {
                        if (currentUser.value?.loginType == "anonymous") {
                            showLoginBottomSheet = true
                        } else {
                            navController.navigate(Routes.ACCOUNT_MANAGEMENT)
                        }
                    },
                    onNavigateToLanguageSetting = {
                        navController.navigate(Routes.LANGUAGE_SETTING)
                    },
                    onNavigateToThemeSetting = {
                        navController.navigate(Routes.THEME_SETTING)
                    },
                    onNavigateToNotificationTimeSetting = {
                        navController.navigate(Routes.NOTIFICATION_TIME_SETTING)
                    },
                    onNavigateToSubscriptionManagement = {
                        navController.navigate(Routes.SUBSCRIPTION_MANAGEMENT)
                    }
                )
            }

            composable(Routes.LANGUAGE_SETTING) {
                LanguageSettingScreen(
                    selectedLanguage = selectedLanguage,
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onLanguageSelected = { language ->
                        selectedLanguageName = language.name
                        appSettingsStorage.setLanguageName(language.name)
                    }
                )
            }

            composable(Routes.THEME_SETTING) {
                ThemeSettingScreen(
                    selectedThemeMode = selectedThemeMode,
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onThemeModeSelected = { themeMode ->
                        selectedThemeModeName = themeMode.name
                        appSettingsStorage.setThemeModeName(themeMode.name)
                    }
                )
            }

            composable(Routes.ACCOUNT_MANAGEMENT) {
                AccountManagementScreen(
                    email = currentUser.value?.email,
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onSignedOut = {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onAccountDeleted = {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.NOTIFICATION_TIME_SETTING) {
                NotificationTimeSettingScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }

                composable(Routes.SUBSCRIPTION_MANAGEMENT) {
                    SubscriptionManagementScreen(
                        onBackPressed = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MyCustomTheme(
    isDarkTheme: Boolean, // 사용자의 선택 값
    content: @Composable () -> Unit
) {
    // 사용자의 선택에 따라 팔레트 교체
    val colors = if (isDarkTheme) DarkColorPalette else LightColorPalette

    CompositionLocalProvider(LocalMyColors provides colors) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBottomNavigation(
    onNavigateToDreamInterpretation: (DreamResponse) -> Unit,
    onNavigateToSettings: () -> Unit
) {

    val navItems = listOf(
        BottomNavItem(stringResource(Res.string.bottom_menu_today_dream_title_text)),
        BottomNavItem(stringResource(Res.string.bottom_menu_dream_calendar_title_text)),
        BottomNavItem(stringResource(Res.string.bottom_menu_today_tarot_title_text)),
//        BottomNavItem(stringResource(Res.string.bottom_menu_insight_title_text)),
//        BottomNavItem(stringResource(Res.string.bottom_menu_my_info_title_text))
    )

    var selectedItemIndex by remember { mutableStateOf(0) }
    val topbarTitle = navItems[selectedItemIndex].label

    Scaffold(
        bottomBar = {

            NavigationBar(
                contentColor = MyTheme.colors.mainBackgroundColor,
                containerColor = MyTheme.colors.mainBackgroundColor
            ) {

                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                        },
                        label = { Text(text = item.label, color = MyTheme.colors.textWhiteColor) },
                        icon = {
//                            Icon(
//                                imageVector = item.icon,
//                                contentDescription = item.label
//                            )
                        }
                    )
                }
            }

        },
        topBar = {

            CenterAlignedTopAppBar(
                title = {
                    Text(text = topbarTitle, color = MyTheme.colors.textWhiteColor, fontFamily = fontFamily(), fontWeight = FontWeight.SemiBold)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarColors(
                    containerColor = MyTheme.colors.mainBackgroundColor,
                    titleContentColor = MyTheme.colors.textWhiteColor,
                    subtitleContentColor = MyTheme.colors.textWhiteColor,
                    scrolledContainerColor = MyTheme.colors.mainBackgroundColor,
                    actionIconContentColor = MyTheme.colors.textWhiteColor,
                    navigationIconContentColor = MyTheme.colors.textWhiteColor
                ),
                actions = {

                    IconButton(onClick = onNavigateToSettings) {

                        Icon(
                            painter = painterResource(Res.drawable.settings)
                            ,contentDescription = stringResource(Res.string.setting_screen_title)
                        )


                    }


                }
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
        modifier = Modifier.fillMaxSize().background(color = Color.White).windowInsetsPadding(WindowInsets.statusBars),
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            when(selectedItemIndex) {
                0 -> DreamRecord(
                    onNavigateToDreamInterpretation = onNavigateToDreamInterpretation
                )
                1 -> DreamCalendar(
                    onNavigateToDreamInterpretation = onNavigateToDreamInterpretation
                )
                2 -> TodayFortuneScreen()
//                2 -> Report()
//                3 -> MyPage()
            }
        }
    }
}
data class BottomNavItem(
    val label: String,
//    val icon: ImageVector
)

private inline fun <reified T : Enum<T>> enumValueOrDefault(
    value: String?,
    defaultValue: String
): String {
    return value
        ?.takeIf { candidate -> enumValues<T>().any { it.name == candidate } }
        ?: defaultValue
}
