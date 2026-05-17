package com.garam.mydream.core.platform

import android.content.Context
import android.content.Intent
import android.net.Uri

class AndroidUrlLauncher(
    private val context: Context
) : UrlLauncher {

    override fun open(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
