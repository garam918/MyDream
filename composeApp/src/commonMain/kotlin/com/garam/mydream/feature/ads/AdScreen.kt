package com.garam.mydream.feature.ads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.lexilabs.basic.ads.DependsOnGoogleMobileAds
import app.lexilabs.basic.ads.composable.BannerAd
import app.lexilabs.basic.ads.composable.rememberBannerAd

@OptIn(DependsOnGoogleMobileAds::class)
@Composable
fun AdScreen() {

    val bannerAd by rememberBannerAd()
    // 광고를 표시할지 숨길지 결정합니다.
    var showBannerAd by remember { mutableStateOf( false ) }

    BannerAd(bannerAd)

}