package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

@One2OneConn[company]("company")
@One2ManyConn[Order]("orders")
@ToStringMacro
case class profile() extends commonEntity {
    var name: String = ""
    var age: Int = 0
    var test1: Seq[Any] = Nil
    var test2: Map[String, String] = Map().empty
    var test3: List[Map[String, String]] = Nil
    var test4: Map[String, Any] = Map().empty
}
