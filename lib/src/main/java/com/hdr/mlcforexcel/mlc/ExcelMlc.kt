package com.hdr.mlcforexcel.mlc

import com.hdr.mlcforexcel.model.Lang
import com.hdr.mlcforexcel.model.Line
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * Created by hdr on 16/11/22.
 */
class ExcelMlc(override val filename: String) : Mlc {
    override val targetIndex = 0
    override val invalidPrefix = emptyList<String>()

    val wb = HSSFWorkbook()
    val sheet by lazy { wb.createSheet() }
    val font by lazy {
        val font = wb.createFont()
        font.fontName = "仿宋_GB2312"
        font.fontHeightInPoints = 12.toShort()
        font
    }

    init {
        //表示有多少列的宽度，因为有那个注释，所以要+1
        val length = Lang.values().size + 1
        sheet.setColumnWidth(0, 10000)
        kotlin.repeat(length) {
            sheet.setColumnWidth(it + 1, 13000)
        }

    }

    override fun handlerLine(index: Int, line: Line) {
        val row = sheet.createRow(index)
        row.height = 600
        val style = wb.createCellStyle()
        style.fillForegroundColor = if (index % 2 == 0) HSSFColor.LIGHT_TURQUOISE.index else HSSFColor.WHITE.index
        style.setBorderRight(BorderStyle.THIN)

        style.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        style.setVerticalAlignment(VerticalAlignment.CENTER)
        if (line.isFirstLine) {
            font.bold = true
            style.setAlignment(HorizontalAlignment.CENTER)
        } else {
            font.bold = false
            style.setAlignment(HorizontalAlignment.LEFT)
        }
        style.setFont(font)
        var cell = row.createCell(0)
        cell.setCellValue(line.key)
        cell.setCellStyle(style)
        val cells = ArrayList<String>().apply {
            add(line.comment)
            addAll(line.values)
        }
        cells.forEachIndexed {
            i, s ->
            cell = row.createCell(i + 1)
            cell.setCellValue(s)
            cell.setCellStyle(style)
        }
    }

    override fun writeFile() {

        val targetFile = File(filename)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        val fileOut = FileOutputStream(targetFile)
        wb.write(fileOut)
        fileOut.close()
    }

}