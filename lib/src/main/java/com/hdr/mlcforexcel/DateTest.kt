package com.hdr.mlcforexcel

import sun.util.resources.ca.LocaleNames_ca
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter

/**
 * Created by hdr on 16/11/29.
 */
fun main(args: Array<String>) {
    val date = Date()

    val wraps = listOf(
            DateWrap(),
            DateWrap("检测界面中，显示的测量时间", "M月d日 a h:m", "MMM d h:m a", "MMM d h:m a", "MM. dd h:m a"),
            DateWrap("日历的抬头，年-月", "yyyy年M月", "MMM yyyy", "yyyy年M月", "yyyy. M"),
            DateWrap("周、月图表中坐标轴显示(月-日)", "M-d", "M/d", "M-d", "M.d"),
            DateWrap("图表中坐标轴显示(年-月-日)", "yy-M-d", "M/d/yy", "yy-M-d", "yy. M. d"),
            DateWrap("日历中用来显示测量数据的时间(时-分)", "a h:m", "h:m a", "a h:m", "h:m a"),
            DateWrap("日历中用来显示测量数据的时间(年 月 日 时-分)", "yy-M-d a h:m", "M/d/yy a h:m", "yyyy-M-d a h:m", "yy. M. d a h:m")
    )
    wraps.forEach {
        wrap ->
        println("|${wrap.formats.map { it.second }.joinToString("|")}|${wrap.comment}|")
        wrap.formats.forEach {
            format ->
            val sdf = if (format.second.isEmpty()) DateFormat.getDateInstance(DateFormat.LONG, format.first) else SimpleDateFormat(format.second, format.first)
            val result = sdf.format(date)
            println(result)
        }
        println()
    }
}

data class DateWrap(
        val comment: String,
        val formats: List<Pair<Locale, String>>
) {
    constructor(comment: String, vararg formats: String) : this(comment, formats.mapIndexed { i, s -> locales[i] to s }.toMutableList())
    constructor() : this("默认日期格式", locales.map { it to "" })

    companion object {
        val locales = listOf(Locale.CHINA, Locale.US, Locale.TAIWAN, Locale.KOREAN)
    }
}