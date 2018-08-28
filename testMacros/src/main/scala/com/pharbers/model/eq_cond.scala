package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, One2OneConn, ToStringMacro}

//@One2OneConn[request]("request")
@ToStringMacro
case class eq_cond() extends commonEntity {
	var key: String = ""
	var value: String = ""
}
