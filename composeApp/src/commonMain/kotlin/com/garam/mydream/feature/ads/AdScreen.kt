package com.garam.mydream.feature.ads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import app.lexilabs.basic.ads.DependsOnGoogleMobileAds
import app.lexilabs.basic.ads.composable.BannerAd
import app.lexilabs.basic.ads.composable.rememberBannerAd
import com.garam.mydream.core.ads.bannerAdUnitId

@OptIn(DependsOnGoogleMobileAds::class)
@Composable
fun AdScreen(adUnitId: String = bannerAdUnitId()) {
    val bannerAd by rememberBannerAd(adUnitId = adUnitId)

    BannerAd(ad = bannerAd)
}
