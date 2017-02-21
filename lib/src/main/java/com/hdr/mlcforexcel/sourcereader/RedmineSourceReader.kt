package com.hdr.mlcforexcel.sourcereader

import com.hdr.mlcforexcel.model.Line
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

/**
 * Created by hdr on 16/11/22.
 */
class RedmineSourceReader(override val filename: String) : SourceReader {
    override fun readAll(): List<Line> {
        val file = File(filename)

        val lineReader = BufferedReader(FileReader(file))
        var lineString: String? = lineReader.readLine()
        val lineList = ArrayList<Line>()
        var firstLine = true
        var commentIndex = -1
        while (lineString != null) {
            val strings = lineString.split("|")
            if (strings.size > 6) {
                val line = Line(strings[1])
                if (firstLine) {
                    firstLine = false
                    line.isFirstLine = true
                    commentIndex = strings.indexOf("备注")
                }
                kotlin.repeat(strings.size - 3) {
                    val index = it + 2
                    if (index != commentIndex)
                        line.values.add(strings[index].trim())
                }
                if (commentIndex >= 0 && commentIndex < strings.size && strings[commentIndex].isNotEmpty()) {
                    line.comment = strings[commentIndex]
                }
                lineList.add(line)
                println(line)
            }
            lineString = lineReader.readLine()
        }
        lineReader.close()
        return lineList
    }

}