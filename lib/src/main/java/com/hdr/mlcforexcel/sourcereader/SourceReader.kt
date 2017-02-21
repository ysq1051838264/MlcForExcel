package com.hdr.mlcforexcel.sourcereader

import com.hdr.mlcforexcel.model.Line

/**
 * Created by hdr on 16/11/22.
 */
interface SourceReader {
    val filename: String
    fun readAll(): List<Line>
}