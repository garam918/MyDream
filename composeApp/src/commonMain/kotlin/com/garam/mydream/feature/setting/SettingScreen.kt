package com.garam.mydream.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.mydream.core.auth.AuthRepositoryProvider
import com.garam.mydream.core.designsystem.MyTheme
import com.garam.mydream.core.designsystem.fontFamily
import com.garam.mydream.core.localization.AppLanguage
import com.garam.mydream.feature.ads.AdScreen
import kotlinx.coroutines.launch
import mydream.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val TERMS_OF_SERVICE_URL = "https://garam-portfolio.netlify.app/mydream/terms/terms"
private const val PRIVACY_POLICY_URL = "https://garam-portfolio.netlify.app/mydream/privacy/privacy"
private const val FEEDBACK_FORM_URL = "https://docs.google.com/forms/d/e/1FAIpQLSeRQkjfMjlL3lcQeE8esWVBJTUOq7kIK2SkRt5ekjD3WtRn9A/viewform?usp=publish-editor"

enum class AppThemeMode {
    LIGHT,
    DARK
}

@Composable
private fun AppLanguage.displayName(): String = when (this) {
    AppLanguage.KOREAN -> stringResource(Res.string.setting_language_korean)
    AppLanguage.ENGLISH -> stringResource(Res.string.setting_language_english)
}

@Composable
private fun AppThemeMode.displayName(): String = when (this) {
    AppThemeMode.LIGHT -> stringResource(Res.string.setting_theme_light)
    AppThemeMode.DARK -> stringResource(Res.string.setting_theme_dark)
}

@Composable
fun SettingScreen(
    email: String?,
    selectedLanguage: AppLanguage,
    selectedThemeMode: AppThemeMode,
    onBackPressed: () -> Unit,
    onNavigateToAccountManagement: () -> Unit,
    onNavigateToLanguageSetting: () -> Unit,
    onNavigateToThemeSetting: () -> Unit,
    onNavigateToNotificationTimeSetting: () -> Unit,
    onNavigateToSubscriptionManagement: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    BaseSettingScaffold(
        title = stringResource(Res.string.setting_screen_title),
        onBackPressed = onBackPressed
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MyTheme.colors.mainBackgroundColor)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                DetailInfoCard(
                    title = stringResource(Res.string.setting_current_account_title),
                    body = if (email.isNullOrBlank()) {
                        stringResource(Res.string.setting_empty_email)
                    } else {
                        email
                    }
                )
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MyTheme.colors.cardBgColor
                ) {
                    SettingMenuItem(
                        title = stringResource(Res.string.setting_feedback_title),
                        subtitle = stringResource(Res.string.setting_feedback_subtitle),
                        showDivider = false,
                        onClick = { uriHandler.openUri(FEEDBACK_FORM_URL) }
                    )
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MyTheme.colors.cardBgColor
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SettingMenuItem(
                            title = stringResource(Res.string.setting_account_management_title),
                            subtitle = stringResource(Res.string.setting_account_management_subtitle),
                            onClick = onNavigateToAccountManagement
                        )
                        SettingMenuItem(
                            title = stringResource(Res.string.setting_language_menu_title),
                            subtitle = stringResource(
                                Res.string.setting_current_selection_format,
                                selectedLanguage.displayName()
                            ),
                            onClick = onNavigateToLanguageSetting
                        )
                        SettingMenuItem(
                            title = stringResource(Res.string.setting_theme_menu_title),
                            subtitle = stringResource(
                                Res.string.setting_current_selection_format,
                                selectedThemeMode.displayName()
                            ),
                            onClick = onNavigateToThemeSetting
                        )
//                        SettingMenuItem(
//                            title = stringResource(Res.string.setting_notification_menu_title),
//                            subtitle = stringResource(Res.string.setting_notification_menu_subtitle),
//                            onClick = onNavigateToNotificationTimeSetting
//                        )
//                        SettingMenuItem(
//                            title = stringResource(Res.string.setting_subscription_menu_title),
//                            subtitle = stringResource(Res.string.setting_subscription_menu_subtitle),
//                            onClick = onNavigateToSubscriptionManagement
//                        )
                        SettingMenuItem(
                            title = stringResource(Res.string.setting_terms_of_service_title),
                            onClick = { uriHandler.openUri(TERMS_OF_SERVICE_URL) }
                        )
                        SettingMenuItem(
                            title = stringResource(Res.string.setting_privacy_policy_title),
                            showDivider = false,
                            onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) }
                        )
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    AdScreen()
                }
            }
        }
    }
}

@Composable
fun LanguageSettingScreen(
    selectedLanguage: AppLanguage,
    onBackPressed: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    BaseSettingScaffold(
        title = stringResource(Res.string.setting_language_menu_title),
        onBackPressed = onBackPressed
    ) { innerPadding ->
        val languages = AppLanguage.values()
        SelectionScreenContent(
            innerPadding = innerPadding,
            title = stringResource(Res.string.setting_language_selection_description)
        ) {
            languages.forEachIndexed { index, language ->
                SelectableOptionItem(
                    title = language.displayName(),
                    subtitle = if (language == AppLanguage.KOREAN) {
                        stringResource(Res.string.setting_language_korean_description)
                    } else {
                        stringResource(Res.string.setting_language_english_description)
                    },
                    selected = selectedLanguage == language,
                    showDivider = index != languages.lastIndex,
                    onClick = {
                        onLanguageSelected(language)
                    }
                )
            }
        }
    }
}

@Composable
fun ThemeSettingScreen(
    selectedThemeMode: AppThemeMode,
    onBackPressed: () -> Unit,
    onThemeModeSelected: (AppThemeMode) -> Unit
) {
    BaseSettingScaffold(
        title = stringResource(Res.string.setting_theme_menu_title),
        onBackPressed = onBackPressed
    ) { innerPadding ->
        val themeModes = AppThemeMode.values()
        SelectionScreenContent(
            innerPadding = innerPadding,
            title = stringResource(Res.string.setting_theme_selection_description)
        ) {
            themeModes.forEachIndexed { index, themeMode ->
                SelectableOptionItem(
                    title = themeMode.displayName(),
                    subtitle = if (themeMode == AppThemeMode.LIGHT) {
                        stringResource(Res.string.setting_theme_light_description)
                    } else {
                        stringResource(Res.string.setting_theme_dark_description)
                    },
                    selected = selectedThemeMode == themeMode,
                    showDivider = index != themeModes.lastIndex,
                    onClick = {
                        onThemeModeSelected(themeMode)
                    }
                )
            }
        }
    }
}

@Composable
fun AccountManagementScreen(
    email: String?,
    onBackPressed: () -> Unit,
    onSignedOut: () -> Unit,
    onAccountDeleted: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepositoryProvider().get() }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    BaseSettingScaffold(
        title = stringResource(Res.string.setting_account_management_title),
        onBackPressed = onBackPressed
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MyTheme.colors.mainBackgroundColor)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            DetailInfoCard(
                title = stringResource(Res.string.setting_logged_in_account_title),
                body = if (email.isNullOrBlank()) {
                    stringResource(Res.string.setting_empty_email)
                } else {
                    email
                }
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MyTheme.colors.cardBgColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            showLogoutDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MyTheme.colors.dreamBtnColor,
                            contentColor = MyTheme.colors.btnTextColor
                        )
                    ) {
                        Text(
                            text = stringResource(Res.string.setting_logout_button),
                            fontFamily = fontFamily(),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            showDeleteDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.setting_delete_account_button),
                            fontFamily = fontFamily(),
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        ConfirmActionDialog(
            title = stringResource(Res.string.setting_logout_dialog_title),
            description = stringResource(Res.string.setting_logout_dialog_description),
            confirmText = stringResource(Res.string.setting_logout_button),
            onDismiss = {
                showLogoutDialog = false
            },
            onConfirm = {
                showLogoutDialog = false
                scope.launch {
                    authRepository.signOut()
                    onSignedOut()
                }
            }
        )
    }

    if (showDeleteDialog) {
        ConfirmActionDialog(
            title = stringResource(Res.string.setting_delete_dialog_title),
            description = stringResource(Res.string.setting_delete_dialog_description),
            confirmText = stringResource(Res.string.setting_delete_account_button),
            confirmColor = Color(0xFFDC2626),
            onDismiss = {
                showDeleteDialog = false
            },
            onConfirm = {
                showDeleteDialog = false
                scope.launch {
                    authRepository.deleteAccount()
                    onAccountDeleted()
                }
            }
        )
    }
}

@Composable
fun NotificationTimeSettingScreen(
    onBackPressed: () -> Unit
) {
    BaseSettingScaffold(
        title = stringResource(Res.string.setting_notification_menu_title),
        onBackPressed = onBackPressed
    ) { innerPadding ->
        PlaceholderDetailScreen(
            innerPadding = innerPadding,
            title = stringResource(Res.string.setting_notification_placeholder_title),
            description = stringResource(Res.string.setting_notification_placeholder_description)
        )
    }
}

@Composable
fun SubscriptionManagementScreen(
    onBackPressed: () -> Unit
) {
    BaseSettingScaffold(
        title = stringResource(Res.string.setting_subscription_menu_title),
        onBackPressed = onBackPressed
    ) { innerPadding ->
        PlaceholderDetailScreen(
            innerPadding = innerPadding,
            title = stringResource(Res.string.setting_subscription_placeholder_title),
            description = stringResource(Res.string.setting_subscription_placeholder_description)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSettingScaffold(
    title: String,
    onBackPressed: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                title = {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = fontFamily(),
                        color = MyTheme.colors.textWhiteColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            painter = painterResource(Res.drawable.back_btn_icon),
                            contentDescription = stringResource(Res.string.common_back),
                            tint = MyTheme.colors.textWhiteColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarColors(
                    containerColor = MyTheme.colors.mainBackgroundColor,
                    titleContentColor = MyTheme.colors.textWhiteColor,
                    navigationIconContentColor = MyTheme.colors.textWhiteColor,
                    actionIconContentColor = MyTheme.colors.textWhiteColor,
                    scrolledContainerColor = MyTheme.colors.mainBackgroundColor,
                    subtitleContentColor = MyTheme.colors.textWhiteColor
                )
            )
        },
        contentWindowInsets = WindowInsets(0),
        contentColor = MyTheme.colors.mainBackgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MyTheme.colors.mainBackgroundColor)
        ) {
            content(innerPadding)
        }
    }
}

@Composable
private fun SettingMenuItem(
    title: String,
    subtitle: String? = null,
    showDivider: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = fontFamily(),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = MyTheme.colors.textWhiteColor
                )

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = subtitle,
                        fontFamily = fontFamily(),
                        fontSize = 13.sp,
                        color = MyTheme.colors.loginExplainTextColor
                    )
                }
            }

            Text(
                text = ">",
                fontFamily = fontFamily(),
                fontSize = 18.sp,
                color = MyTheme.colors.secondaryColor.copy(alpha = 0.7f)
            )
        }

        if (showDivider) {
            Spacer(modifier = Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MyTheme.colors.textWhiteColor.copy(alpha = 0.08f))
            )
        }
    }
}

@Composable
private fun SelectionScreenContent(
    innerPadding: PaddingValues,
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTheme.colors.mainBackgroundColor)
            .padding(innerPadding)
            .padding(horizontal = 20.dp,),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        DetailInfoCard(
            title = stringResource(Res.string.common_notice),
            body = title
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MyTheme.colors.cardBgColor
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun SelectableOptionItem(
    title: String,
    subtitle: String,
    selected: Boolean,
    showDivider: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = fontFamily(),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = MyTheme.colors.textWhiteColor
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    fontFamily = fontFamily(),
                    fontSize = 13.sp,
                    color = MyTheme.colors.loginExplainTextColor
                )
            }

            RadioButton(
                selected = selected,
                onClick = onClick
            )
        }

        if (showDivider) {
            Spacer(modifier = Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MyTheme.colors.textWhiteColor.copy(alpha = 0.08f))
            )
        }
    }
}

@Composable
private fun DetailInfoCard(
    title: String,
    body: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MyTheme.colors.cardBgColor
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                fontFamily = fontFamily(),
                fontSize = 13.sp,
                color = MyTheme.colors.loginExplainTextColor
            )
            Text(
                text = body,
                fontFamily = fontFamily(),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MyTheme.colors.textWhiteColor
            )
        }
    }
}

@Composable
private fun PlaceholderDetailScreen(
    innerPadding: PaddingValues,
    title: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MyTheme.colors.mainBackgroundColor)
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MyTheme.colors.cardBgColor
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = title,
                    fontFamily = fontFamily(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MyTheme.colors.textWhiteColor,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = description,
                    fontFamily = fontFamily(),
                    fontSize = 14.sp,
                    color = MyTheme.colors.loginExplainTextColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ConfirmActionDialog(
    title: String,
    description: String,
    confirmText: String,
    confirmColor: Color? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val resolvedConfirmColor = confirmColor ?: MyTheme.colors.dreamBtnColor

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontFamily = fontFamily(),
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = description,
                fontFamily = fontFamily(),
                color = MyTheme.colors.loginExplainTextColor
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = resolvedConfirmColor)
            ) {
                Text(
                    text = confirmText,
                    fontFamily = fontFamily()
                )
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(
                    text = stringResource(Res.string.common_cancel),
                    fontFamily = fontFamily()
                )
            }
        }
    )
}
