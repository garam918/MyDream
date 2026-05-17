package com.garam.mydream.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import mydream.composeapp.generated.resources.Res
import mydream.composeapp.generated.resources.wanted_sans_bold
import mydream.composeapp.generated.resources.wanted_sans_extra_bold
import mydream.composeapp.generated.resources.wanted_sans_medium
import mydream.composeapp.generated.resources.wanted_sans_regular
import mydream.composeapp.generated.resources.wanted_sans_semi_bold
import org.jetbrains.compose.resources.Font


@Composable
fun fontFamily() : FontFamily = FontFamily(

    Font(
        resource = Res.font.wanted_sans_medium,
        weight = FontWeight.Medium
    ),
    Font(
        resource = Res.font.wanted_sans_regular,
        weight = FontWeight.Normal
    ),
    Font(
        resource = Res.font.wanted_sans_semi_bold,
        weight = FontWeight.SemiBold
    ),
    Font(
        resource = Res.font.wanted_sans_bold,
        weight = FontWeight.Bold
    ),
    Font(
        resource = Res.font.wanted_sans_extra_bold,
        weight = FontWeight.ExtraBold
    ),
//    Font(
//        resource = Res.font,
//        weight = FontWeight.Medium
//    ),

)