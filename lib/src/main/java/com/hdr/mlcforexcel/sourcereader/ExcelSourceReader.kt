package com.hdr.mlcforexcel.sourcereader

import com.hdr.mlcforexcel.model.Line
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.File
import java.util.*

/**
 * Created by hdr on 16/11/22.
 */
class ExcelSourceReader(override val filename: String) : SourceReader {
    override fun readAll(): List<Line> {
        val file = File(filename)
        val lineList = ArrayList<Line>()

        val pfs = POIFSFileSystem(file)
        val wb = HSSFWorkbook(pfs)
        val sheet = wb.getSheetAt(0)
        var commentIndex = -1
        for (index in 0..sheet.lastRowNum) {
            val row = sheet.getRow(index)
            if (row.getCell(0) == null || row.getCell(0).stringCellValue.isBlank()) {
                break
            }
            val line = Line(row.getCell(0).stringCellValue)
            if (index === 0) {
                line.isFirstLine = true
            }
            kotlin.repeat(row.lastCellNum.toInt()) {
                val colIndex = it + 1
                val content = row.getCell(colIndex)?.stringCellValue?.trim() ?: ""

                if (line.isFirstLine && content == "备注") {
                    commentIndex = colIndex
                }
                if (commentIndex == colIndex) {
                    line.comment = content
                } else
                    line.values.add(content)
            }
            lineList.add(line)
        }
        return lineList
    }

}