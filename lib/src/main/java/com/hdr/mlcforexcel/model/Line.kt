package com.hdr.mlcforexcel.model

import java.util.*

/**
 * Created by hdr on 16/11/21.
 */
data class Line(
        var key: String,
        var values: MutableList<String> = ArrayList(),
        var comment: String = "",
        var isFirstLine: Boolean = false
)