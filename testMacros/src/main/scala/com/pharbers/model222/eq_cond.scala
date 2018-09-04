package com.pharbers.model222

import com.mongodb.casbah.Imports._
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro
import com.pharbers.macros.convert.mongodb.TraitConditions

//@ToStringMacro
case class eq_cond() extends commonEntity with TraitConditions {
	var key: String = ""
	var value: Any = null
	
	override def cond2QueryDBObject(): DBObject = DBObject(key -> value)
	
	override def cond2UpdateDBObectj(): DBObject = DBObject()
	
	override def isQueryCond: Boolean = true
	
	override def isUpdateCond: Boolean = false
}