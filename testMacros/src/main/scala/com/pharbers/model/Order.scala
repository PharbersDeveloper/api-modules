package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
case class Order() extends commonEntity {
    var title: String = ""
    val abc: Int = 1234
}
