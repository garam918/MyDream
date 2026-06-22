package com.garam.mydream.feature.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garam.mydream.core.designsystem.MyTheme
import com.garam.mydream.feature.login.LoginBottomSheetScreen
import mydream.composeapp.generated.resources.Res
import com.garam.mydream.core.designsystem.fontFamily
import mydream.composeapp.generated.resources.onboarding_app_explain_text
import mydream.composeapp.generated.resources.onboarding_app_name_text
import mydream.composeapp.generated.resources.onboarding_img
import mydream.composeapp.generated.resources.onboarding_start_btn_text
import mydream.composeapp.generated.resources.onboarding_top_text
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnboardingScreen(onLoginSuccess : () -> Unit) {

    var isLoginBottomSheetShow by remember { mutableStateOf(false) }

    if(isLoginBottomSheetShow) LoginBottomSheetScreen(
        onDismiss = {
            isLoginBottomSheetShow = false
        },
        onSuccessLogin = {
            onLoginSuccess()
        }
    )

    Scaffold(
        contentColor = MyTheme.colors.onboardingBGColor,
        containerColor = MyTheme.colors.onboardingBGColor,
        bottomBar = {
            TextButton(
                onClick = {
                    isLoginBottomSheetShow = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyTheme.colors.onboardingBtnBGColor,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(Res.string.onboarding_start_btn_text),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontFamily = fontFamily()
                )
            }
        }
    ) { innerPadding ->

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            val isCompactHeight = maxHeight < 700.dp
            val onboardingImageSize = when {
                isCompactHeight -> 220.dp
                maxWidth > 600.dp -> 320.dp
                else -> 300.dp
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {

                Spacer(modifier = Modifier.height(if (isCompactHeight) 24.dp else 70.dp))

                Image(
                    painter = painterResource(Res.drawable.onboarding_img),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier.size(onboardingImageSize).clip(shape = CircleShape)
                )

                Spacer(modifier = Modifier.height(if (isCompactHeight) 20.dp else 32.dp))

                Row(
                    modifier = Modifier.background(
                        color = MyTheme.colors.onboardingBGColor,
                    ).border(width = 1.dp,color = Color.Gray, shape = RoundedCornerShape(24.dp)).padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Canvas(
                        modifier = Modifier
                            .size(8.dp) // 원의 크기
                    ) {
                        drawCircle(
                            color = Color(0xFFEBC77D), // 노란색/골드색
                            radius = size.minDimension / 2 // 캔버스 크기에 맞는 반지름
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(Res.string.onboarding_top_text),
                        textAlign = TextAlign.Center,
                        fontFamily = fontFamily(),
                        color = Color.Gray
                    )
                }



                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.onboarding_app_name_text),
                    color = MyTheme.colors.textWhiteColor,
                    textAlign = TextAlign.Center,
                    fontFamily = fontFamily(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 36.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.onboarding_app_explain_text),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontFamily = fontFamily()
                )

                Spacer(modifier = Modifier.height(24.dp))

            }
        }

    }


}
