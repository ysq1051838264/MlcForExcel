package com.hdr.mlcforexcel.mlc

import com.hdr.mlcforexcel.model.Lang
import com.hdr.mlcforexcel.model.Line
import com.hdr.mlcforexcel.uilts.stringWithoutEscapeChars
import org.dom4j.DocumentHelper
import org.dom4j.dom.DOMComment
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter
import java.io.File
import java.io.FileOutputStream

/**
 * 安卓的转化器
 * Created by hdr on 16/11/21.
 */
class AndroidMlc(override val filename: String, override val targetIndex: Int) : Mlc {
    val doc = DocumentHelper.createDocument()
    val root = doc.addElement("resources")
    override val invalidPrefix: List<String>
        get() = listOf("key", "IOS", "server")

    override fun handlerLine(index: Int, line: Line) {
        if (line.comment.isNotEmpty()) {
            root.add(DOMComment(line.comment))
        }
        if (line.values[targetIndex].stringWithoutEscapeChars != "") {
            val ele = root.addElement("string")
            ele.addAttribute("name", line.key)
            ele.addText(line.values[targetIndex].stringWithoutEscapeChars)
        }
    }

    override fun writeFile() {
        val file = File(filename)
        File(file.parent).apply { if (!exists()) mkdirs() }
        if (file.exists()) {
            file.delete()
        }
        val fos = FileOutputStream(file)
        val of = OutputFormat()
        of.indent = "\t"
        of.isNewlines = true
        val xmlWriter = XMLWriter(fos, of)
        xmlWriter.isEscapeText = false
        xmlWriter.write(doc)
        xmlWriter.flush()
        xmlWriter.close()
    }

    companion object {
        fun filename(targetDic: String, lang: Lang): String {
            val subDir = when (lang) {
                Lang.SimplifiedChinese -> {
                    "-zh-rCN"
                }
                Lang.TraditionalChinese -> {
                    "-zh-rTW"
                }
                Lang.Korean -> {
                    "-ko-rKR"
                }
                Lang.Japanese -> {
                    "-ja-rJP"
                }
                Lang.German -> {
                    "-de-rDE"
                }
                Lang.French -> {
                    "-fr-rFR"
                }
                Lang.Russian -> {
                    "-ru-rRU"
                }
                Lang.Spanish -> {
                    "-es-rES"
                }
                Lang.Portuguese -> {
                    "-pt-rPT"
                }
                Lang.Arabic -> {
                    "-ar-rEG"
                }
                Lang.Czech -> {
                    "-cs-rCZ"
                }
                Lang.Italian -> {
                    "-it-rIT"
                }
                else -> {
                    ""
                }
            }
            return "${targetDic}android/values$subDir/strings.xml"
        }
    }

}