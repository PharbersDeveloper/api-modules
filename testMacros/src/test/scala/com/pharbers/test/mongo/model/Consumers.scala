package com.pharbers.test.mongo.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}
import com.pharbers.macros.convert.mongodb.MongoMacro

@One2ManyConn[Order]("orders")
@ToStringMacro
case class Consumers() extends commonEntity {
	var name: String = ""
	var age: Int = 0
	var phone: String = ""

	
}
