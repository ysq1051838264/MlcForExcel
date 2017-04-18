package com.hdr.mlcforexcel.prehandler

import com.spreada.utils.chinese.ZHConverter

/**
 * Created by hdr on 17/3/20.
 */
object TranditionalPrehandler : PreHandler {
    override fun handle(values: MutableList<String>) {
        if (values[2].isEmpty()) {
            values[2] = ZHConverter.convert(values[0], ZHConverter.TRADITIONAL)
        }
    }

}