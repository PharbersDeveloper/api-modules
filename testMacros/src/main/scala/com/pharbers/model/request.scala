package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._

@One2ManyConn[conditions]("conditions")
@ToStringMacro
case class request() extends commonEntity {
   var res: String = ""
}
