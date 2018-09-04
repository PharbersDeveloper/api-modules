package com.pharbers.pattern.mongo.model

import com.mongodb.casbah.Imports.DBObject
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro
import com.pharbers.macros.convert.mongodb.TraitConditions

@ToStringMacro
case class up_cond() extends commonEntity with TraitConditions {
	var key: String = ""
	var value: Any = null

	override def cond2QueryDBObject(): DBObject = DBObject()

	override def cond2UpdateDBObectj(): DBObject = DBObject(key -> value)

	override def isQueryCond: Boolean = false

	override def isUpdateCond: Boolean = true
}
