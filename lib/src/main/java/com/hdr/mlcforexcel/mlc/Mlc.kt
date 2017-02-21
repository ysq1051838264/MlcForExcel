package com.hdr.mlcforexcel.mlc

import com.hdr.mlcforexcel.model.Line

/**
 * Created by hdr on 16/11/21.
 */

interface Mlc {
    val filename: String

    val targetIndex: Int

    val invalidPrefix: List<String>

    fun handlerLine(index: Int, line: Line)

    fun writeFile()

}
