package com.hdr.mlcforexcel

import com.hdr.mlcforexcel.mlc.*
import com.hdr.mlcforexcel.model.App
import com.hdr.mlcforexcel.model.Lang
import com.hdr.mlcforexcel.model.Line
import com.hdr.mlcforexcel.prehandler.AppNamePreHandler
import com.hdr.mlcforexcel.prehandler.TranditionalPrehandler
import com.hdr.mlcforexcel.sourcereader.ExcelSourceReader
import com.hdr.mlcforexcel.sourcereader.RedmineSourceReader
import com.hdr.mlcforexcel.sourcereader.SourceReader
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

    var lineList = sourceReader.readAll() as ArrayList<Line>

    val targetDir = filePath.substring(0, filePath.lastIndexOf("/")) + "/mlc/"
    File(targetDir).apply { if (!exists()) mkdir() }

    val langs = Lang.values()
    val mls = ArrayList<Mlc>()
    mls.add(ExcelMlc(targetDir + "多语言.xls"))
    mls.add(RedmineMlc(targetDir + "多语言.txt"))

//    val appNames = AppName.values()

    val appNames = ArrayList<String>()
    appNames.add("yolanda")
    appNames.add("kitnew")
    appNames.add("RENPHO")
    appNames.add("feelfit")
    appNames.add("BF Scale")
    appNames.add("TT healthier")

    val traPreHandler = TranditionalPrehandler

    lineList.forEach {
        line ->
        traPreHandler.handle(line.values)
    }

    val appList = ArrayList<App>()

    appNames.forEachIndexed {
        i, name ->
        val app = App(preHandler = AppNamePreHandler("yolanda", name.toString()))
        langs.forEachIndexed { index, lang ->
            app.mlcList.add(AndroidMlc(AndroidMlc.filename(targetDir + name + "/", lang), index))
            app.mlcList.add(IOSMlc(IOSMlc.filename(targetDir + name + "/", lang), index))
        }
        appList.add(app)
        lineList.forEach {
            line ->
            val _line = line.copy(values = line.values.map { it }.toMutableList())
            app.preHandler.handle(_line.values)
            app.lineList.add(_line)
        }
    }

    appList.forEach {
        app ->
        app.lineList.forEachIndexed {
            index,line ->
            app.mlcList.forEach {
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
        app.mlcList.forEach(Mlc::writeFile)
    }

    lineList.forEachIndexed {
        index, line ->
//        val pres = arrayOf(TranditionalPrehandler, AppNamePreHandler("feelfit","yolanda"))
//        pres.forEach {
//            handler ->
//            handler.handle(line.values)
//        }

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
