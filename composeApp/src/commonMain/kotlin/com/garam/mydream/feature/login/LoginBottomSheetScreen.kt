package com.garam.mydream.feature.login

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.mydream.core.auth.AuthRepositoryProvider
import com.garam.mydream.core.auth.rememberGoogleAuthHandler
import com.garam.mydream.core.platform.getPlatform
import com.garam.mydream.core.designsystem.MyTheme
import com.garam.mydream.core.designsystem.fontFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mydream.composeapp.generated.resources.Res
import mydream.composeapp.generated.resources.apple_login_icon
import mydream.composeapp.generated.resources.google_login_icon
import mydream.composeapp.generated.resources.login_bottom_sheet_apple_login_btn_text
import mydream.composeapp.generated.resources.login_bottom_sheet_explain_text
import mydream.composeapp.generated.resources.login_bottom_sheet_google_login_btn_text
import mydream.composeapp.generated.resources.login_bottom_sheet_guest_login_btn_text
import mydream.composeapp.generated.resources.login_bottom_sheet_title_text
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.collections.get
import kotlin.text.get


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBottomSheetScreen(
    onDismiss: () -> Unit,
    onSuccessLogin : () -> Unit,
    showGuestContinueButton: Boolean = true,
    loginViewModel: LoginViewModel = koinViewModel()
) {
    val sheetState = rememberModalBottomSheetState()

    val repo = AuthRepositoryProvider()

    val googleAuthHandler = rememberGoogleAuthHandler()
    val googleLoginScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()

                           },
        sheetState = sheetState,
        contentColor = Color.White,
        containerColor = Color.White

    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {

            Text(
                text = stringResource(Res.string.login_bottom_sheet_title_text),
                fontFamily = fontFamily(),
                textAlign = TextAlign.Center,
                color = MyTheme.colors.loginTopTextColor,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(Res.string.login_bottom_sheet_explain_text),
                fontFamily = fontFamily(),
                textAlign = TextAlign.Center,
                color = MyTheme.colors.loginExplainTextColor,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            SocialLoginBtn(onClick = {

                googleLoginScope.launch {
                    runCatching {
                        val tokenList = googleAuthHandler.signIn()
                        val idToken = tokenList.getOrNull(0)
                        val accessToken = tokenList.getOrNull(1).orEmpty()

                        if(!idToken.isNullOrBlank()) {
                            val userData = repo.get().signInWithGoogle(idToken, accessToken)

                            if (userData != null) {
                                val isCompleted = loginViewModel.completeSocialLogin(userData)

                                if (isCompleted) {
                                    onSuccessLogin()
                                }
                            }

//                        if(loginScreenType != "DeleteAccount") {

//                        }
                        } else {
                            println("Google login token is empty")
                        }
                    }.onFailure {
                        println("Google login failed: ${it.message}")
                    }
                }

//                repo.get().signInWithGoogle()

            }, btnText = stringResource(Res.string.login_bottom_sheet_google_login_btn_text)
                , icon = Res.drawable.google_login_icon, textColor = Color.Black, btnBgColor = Color.White)

            Spacer(modifier = Modifier.height(24.dp))

            if(getPlatform().name == "iOS") {
                val appleLoginScope = rememberCoroutineScope()

                SocialLoginBtn(onClick = {

                    appleLoginScope.launch {

                        val currentUser = repo.get().currentUser()
                        val user = if(currentUser == null) repo.get().signInWithApple()
                        else repo.get().linkInWithApple()

                        val resolvedUser = user ?: if (currentUser != null) repo.get().signInWithApple() else null

                        if (resolvedUser != null) {
                            val isCompleted = loginViewModel.completeSocialLogin(resolvedUser)

                            if (isCompleted) {
                                onSuccessLogin()
                            }
                        } else {
                            println("apple user null")
                        }

                    }

//                repo.get().signInWithApple()

                }, btnText = stringResource(Res.string.login_bottom_sheet_apple_login_btn_text)
                    , icon = Res.drawable.apple_login_icon, textColor = Color.White, btnBgColor = Color.Black)
            }

            if (showGuestContinueButton) {
                Text(
                    text = stringResource(Res.string.login_bottom_sheet_guest_login_btn_text),
                    fontFamily = fontFamily(),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.clickable(true, onClick = {
                        CoroutineScope(Dispatchers.Main).launch {
                            val user = repo.get().signInAnonymously()

                            if (user != null) {
                                println(user.toString())

                                loginViewModel.saveUserData(user).invokeOnCompletion {
                                    onSuccessLogin()
                                }
                            }
                        }

                    })
                )
            }



            // 이용약관, 개인정보 처리방침

        }


    }
}

@Composable
fun SocialLoginBtn(onClick: () -> Unit, btnText: String, icon: DrawableResource, textColor: Color, btnBgColor : Color) {

    Button(onClick = {

        onClick()

    }, modifier = Modifier.fillMaxWidth().border(width = 1.dp, color = Color(0xFFE5E7EB), shape = RoundedCornerShape(24.dp))
        , shape = RoundedCornerShape(24.dp)
        , colors = ButtonDefaults.buttonColors(contentColor = btnBgColor, containerColor = btnBgColor)
    ) {

        Icon(
            painter = painterResource(icon),
            contentDescription = ""
        )

        Text(
            text = btnText,
            fontFamily = fontFamily(),
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)

        )

    }

}
