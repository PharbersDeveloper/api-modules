package com.pharbers.mongodb

import com.mongodb.casbah.Imports._
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
case class up_cond() extends commonEntity with TraitConditions {
	var key: String = ""
	var value: String = ""

	override def cond2QueryDBObject(): DBObject = DBObject()

	override def cond2UpdateDBObectj(): DBObject = DBObject(key -> value)

	override def isQueryCond(): Boolean = false

	override def isUpdateCond(): Boolean = true

	def queryConnect(): DBObject = DBObject()
}
