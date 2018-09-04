package com.pharbers.test.mongo.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
case class Order() extends commonEntity {
	var title: String = ""
	var abc: Int = 1234
}
