package com.pharbers.mongodb

import com.mongodb.casbah.Imports._
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
case class eq_cond() extends commonEntity with Conditions {
	var key: String = ""
	var value: String = ""
	
	override def cond2QueryDBObject(): DBObject = {
		DBObject(key -> value)
	}
	
	override def cond2UpdateDBObectj(): DBObject = DBObject()
	
	override def isQueryCond(): Boolean = true
	
	override def isUpdataCond(): Boolean = false
	
	def queryConnect(): DBObject = DBObject()
}
