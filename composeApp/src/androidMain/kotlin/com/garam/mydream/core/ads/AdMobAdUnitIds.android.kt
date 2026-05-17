package com.garam.mydream.core.ads

import android.content.pm.ApplicationInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import app.lexilabs.basic.ads.AdUnitId
import mydream.composeapp.generated.resources.Res
import mydream.composeapp.generated.resources.admob_banner_ad_unit_id
import mydream.composeapp.generated.resources.admob_rewarded_ad_unit_id
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun bannerAdUnitId(): String {
    if (isDebuggableApp()) {
        return AdUnitId.BANNER_DEFAULT
    }

    return stringResource(Res.string.admob_banner_ad_unit_id)
}

@Composable
internal actual fun rewardedAdUnitId(): String {
    if (isDebuggableApp()) {
        return AdUnitId.REWARDED_DEFAULT
    }

    return stringResource(Res.string.admob_rewarded_ad_unit_id)
}

@Composable
private fun isDebuggableApp(): Boolean {
    val applicationInfo = LocalContext.current.applicationInfo
    return applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}
