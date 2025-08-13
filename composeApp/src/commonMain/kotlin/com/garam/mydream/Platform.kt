package com.garam.mydream

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform