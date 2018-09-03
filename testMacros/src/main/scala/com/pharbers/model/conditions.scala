package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
case class conditions() extends commonEntity { //  extends commonEntity
    var key: String =  ""
    var `val`: String = ""
}
