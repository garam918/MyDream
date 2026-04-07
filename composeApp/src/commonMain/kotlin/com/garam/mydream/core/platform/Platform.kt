package com.garam.mydream.core.platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
