package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@One2OneConn[abc]("abc")
@One2ManyConn[Order]("orders")
@ToStringMacro
case class profile() extends commonEntity {
    var name: String = ""
    var age: Int = 0
}
