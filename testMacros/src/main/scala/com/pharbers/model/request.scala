package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@One2ManyConn[eq_cond]("eq_cond")
@ToStringMacro
case class request() extends commonEntity{
	var res: String = ""
}
