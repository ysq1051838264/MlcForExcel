package com.hdr.mlcforexcel.mlc

import com.hdr.mlcforexcel.model.Lang
import com.hdr.mlcforexcel.model.Line
import java.io.File

/**
 * Created by hdr on 16/11/21.
 */
class IOSMlc(override val filename: String, override val targetIndex: Int) : Mlc {

    override val invalidPrefix: List<String>
        get() = listOf("key", "android", "server")

    val sb = StringBuilder()

    override fun handlerLine(index: Int, line: Line) {
        val content = line.values[targetIndex].replace("\"", "\\\"")
        sb.append("\"${line.key}\" = \"$content\";")
        if (line.comment.isNotEmpty()) {
            sb.append("//${line.comment}")
        }
        sb.append("\n")
    }

    override fun writeFile() {
        val file = File(filename)
        File(file.parent).apply { if (!exists()) mkdir() }

        file.writeText(sb.toString())
    }

    companion object {
        fun filename(targetDic: String, lang: Lang): String {
            val fn = when (lang) {
                Lang.SimplifiedChinese -> {
                    "Chinesse_simple"
                }
                Lang.TraditionalChinese -> {
                    "Chinese_traditional"
                }
                Lang.Korean -> {
                    "Korean"
                }
                Lang.Japanese -> {
                    "Japanese"
                }
                Lang.German -> {
                    "German"
                }
                Lang.French -> {
                    "French"
                }
                Lang.Russian -> {
                    "Russian"
                }
                Lang.Spanish -> {
                    "Spanish"
                }
                Lang.Portuguese -> {
                    "Portuguese"
                }
                Lang.Arabic -> {
                    "Arabic"
                }
                else -> {
                    "english"
                }
            }
            return "${targetDic}ios/$fn.strings"
        }
    }
}