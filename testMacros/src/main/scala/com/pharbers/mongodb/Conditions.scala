package com.pharbers.mongodb

import com.mongodb.casbah.Imports._

trait Conditions {
	def cond2QueryDBObject(): DBObject
	def cond2UpdateDBObectj(): DBObject
	def isQueryCond(): Boolean
	def isUpdataCond(): Boolean
}
