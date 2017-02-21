package com.hdr.mlcforexcel.mlc

import com.hdr.mlcforexcel.model.Line
import java.io.File

/**
 * Created by hdr on 16/11/22.
 */
class RedmineMlc(override val filename: String) : Mlc {
    override val targetIndex = 0
    override val invalidPrefix = emptyList<String>()

    val sb = StringBuilder()

    init {
        sb.append("h1. 多语言\n")
        sb.append("\n")
    }

    override fun handlerLine(index: Int, line: Line) {
        sb.append("|${line.key}|")
        line.values.forEach {
            sb.append("$it|")
        }
        sb.append("${line.comment}|")
        sb.append("\n")
    }

    override fun writeFile() {
        val file = File(filename)
        File(file.parent).apply { if (!exists()) mkdir() }

        file.writeText(sb.toString())
    }
}