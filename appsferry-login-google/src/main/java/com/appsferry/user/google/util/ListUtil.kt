package com.appsferry.user.google.util

object ListUtil {
    private const val EMAIL = "email"

    @JvmStatic
    val requestedScopes: List<String>
        get() {
            val list: MutableList<String> = ArrayList()
            list.add(EMAIL)
            return list
        }
}