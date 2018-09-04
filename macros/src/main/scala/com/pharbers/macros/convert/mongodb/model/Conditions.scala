package com.pharbers.macros.convert.mongodb.model

import com.mongodb.casbah.Imports._
import com.pharbers.macros.api.commonEntity

trait TraitConditions {
	def cond2QueryDBObject(): DBObject = DBObject()
	def cond2UpdateDBObectj(): DBObject = DBObject()
	def isQueryCond: Boolean = false
	def isUpdateCond: Boolean = false
}

case class Conditions() extends commonEntity with TraitConditions
