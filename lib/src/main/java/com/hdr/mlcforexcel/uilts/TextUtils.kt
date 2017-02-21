package com.hdr.mlcforexcel.uilts

/**
 * Created by hdr on 16/11/21.
 */

val xmlEscapeChars by lazy {
    mapOf(
            "&" to "&amp;",
            "<" to "&lt;",
            ">" to "&gt;",
            "\"" to "\\&quot;",
            "'" to "\\&apos;")
}

val String.stringWithoutEscapeChars: String
    get() {
        var str = this
        xmlEscapeChars.forEach {
            str = str.replace(it.key, it.value)
        }
        return str
    }