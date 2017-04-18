package com.hdr.mlcforexcel.prehandler

/**
 * Created by hdr on 17/3/20.
 */
class AppNamePreHandler(val originName: String, val targetName: String) : PreHandler {
    val allUppercaseName by lazy { arrayOf(originName.toUpperCase(), targetName.toUpperCase()) }
    val allLowercaseName by lazy { arrayOf(originName.toLowerCase(), targetName.toLowerCase()) }
    val firstUppercaseName by lazy { arrayOf(originName.toFirstUppercase(), targetName.toFirstUppercase()) }

    fun String.toFirstUppercase(): String {
        val sb = StringBuilder()
        sb.append(this[0].toUpperCase())
        sb.append(this.substring(1))
        return sb.toString()
    }

    override fun handle(values: MutableList<String>) {
        if (originName == targetName) {
            return
        }

        //如果传入的大写,全部换成大写
        if (targetName == allUppercaseName[1]) {
            allLowercaseName[1] = allUppercaseName[1]
            firstUppercaseName[1] = allUppercaseName[1]
        }

        for (i in 0..values.lastIndex) {
            val str = values[i]
            values[i] = str.replace(allUppercaseName[0], allUppercaseName[1])
                    .replace(allLowercaseName[0], allLowercaseName[1])
                    .replace(firstUppercaseName[0], firstUppercaseName[1])

        }

    }

}