package com.garam.mydream.core.ads

import androidx.compose.runtime.Composable
import app.lexilabs.basic.ads.AdUnitId
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform
import mydream.composeapp.generated.resources.Res
import mydream.composeapp.generated.resources.admob_banner_ad_unit_id
import mydream.composeapp.generated.resources.admob_home_banner_ad_unit_id
import mydream.composeapp.generated.resources.admob_rewarded_ad_unit_id
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalNativeApi::class)
@Composable
internal actual fun bannerAdUnitId(): String {
    if (Platform.isDebugBinary) {
        return AdUnitId.BANNER_DEFAULT
    }

    return stringResource(Res.string.admob_banner_ad_unit_id)
}

@OptIn(ExperimentalNativeApi::class)
@Composable
internal actual fun homeBannerAdUnitId(): String {
    if (Platform.isDebugBinary) {
        return AdUnitId.BANNER_DEFAULT
    }

    return stringResource(Res.string.admob_home_banner_ad_unit_id)
}

@OptIn(ExperimentalNativeApi::class)
@Composable
internal actual fun rewardedAdUnitId(): String {
    if (Platform.isDebugBinary) {
        return AdUnitId.REWARDED_DEFAULT
    }

    return stringResource(Res.string.admob_rewarded_ad_unit_id)
}
