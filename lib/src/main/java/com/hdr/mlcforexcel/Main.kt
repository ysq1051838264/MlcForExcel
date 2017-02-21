package com.hdr.mlcforexcel

import com.hdr.mlcforexcel.mlc.*
import com.hdr.mlcforexcel.model.Lang
import com.hdr.mlcforexcel.sourcereader.ExcelSourceReader
import com.hdr.mlcforexcel.sourcereader.RedmineSourceReader
import com.hdr.mlcforexcel.sourcereader.SourceReader
import com.spreada.utils.chinese.ZHConverter
import java.io.File
import java.util.*

/**
 * 路径
 * Created by hdr on 16/11/21.
 */


fun main(argv: Array<String>) {
    if (argv.size == 0) {
        error("请输入文件的路径")
        return
    }
    val filePath = argv[0]
    val file = File(filePath)
    if (!file.exists() || file.isDirectory || !file.canRead()) {
        error("文件不存在或不可读")
        return
    }
    val sourceReader: SourceReader
    if (filePath.endsWith(".txt")) {
        sourceReader = RedmineSourceReader(filePath)
    } else if (filePath.endsWith(".xls")) {
        sourceReader = ExcelSourceReader(filePath)
    } else {
        error("文件格式错误")
        return
    }

    val lineList = sourceReader.readAll()

    val targetDir = filePath.substring(0, filePath.lastIndexOf("/")) + "/mlc/"
    File(targetDir).apply { if (!exists()) mkdir() }

    val langs = Lang.values()
    val mls = ArrayList<Mlc>()
    mls.add(ExcelMlc(targetDir + "多语言.xls"))
    mls.add(RedmineMlc(targetDir + "多语言.txt"))

    lineList.forEach {
        if (it.values[2].isEmpty()) {
            it.values[2] = ZHConverter.convert(it.values[0], ZHConverter.TRADITIONAL)
        }
    }

    langs.forEachIndexed { index, lang ->
        mls.add(AndroidMlc(AndroidMlc.filename(targetDir, lang), index))
        mls.add(IOSMlc(IOSMlc.filename(targetDir, lang), index))
    }
    lineList.forEachIndexed {
        index, line ->
        mls.forEach {

            var valid = true

            it.invalidPrefix.forEach {
                prefix ->
                if (line.key.startsWith(prefix)) {
                    valid = false
                    return@forEach
                }
            }
            if (valid)
                it.handlerLine(index, line)
        }
    }
    mls.forEach(Mlc::writeFile)
}