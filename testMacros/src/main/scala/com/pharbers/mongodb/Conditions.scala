package com.pharbers.mongodb

import com.mongodb.casbah.Imports._
import com.pharbers.macros.api.commonEntity

//trait Conditions {
//	def cond2QueryDBObject(): DBObject
//	def cond2UpdateDBObectj(): DBObject
//	def isQueryCond(): Boolean
//	def isUpdateCond(): Boolean
//}

trait TraitConditions {
	def cond2QueryDBObject(): DBObject = DBObject()
	def cond2UpdateDBObectj(): DBObject = DBObject()
	def isQueryCond(): Boolean = false
	def isUpdateCond(): Boolean = false
}

case class Conditions() extends commonEntity with TraitConditions
