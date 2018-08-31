package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@One2ManyConn[profile]("users")
@ToStringMacro
case class company() extends commonEntity {
    var company_name: String = ""
}
