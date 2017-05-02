package com.hdr.mlcforexcel.model

import com.hdr.mlcforexcel.mlc.Mlc
import com.hdr.mlcforexcel.prehandler.PreHandler
import java.util.*

/**
 * Created by ysq on 17/5/2.
 */

data class App(
        val lineList: ArrayList<Line> = ArrayList(),
        val mlcList: ArrayList<Mlc> = ArrayList(),
        val preHandler: PreHandler
)
