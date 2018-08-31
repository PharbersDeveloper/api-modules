package com.pharbers.mongodb

import com.mongodb.casbah.Imports._
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@One2ManyConn[eq_cond]("conditions")
@ToStringMacro
case class request() extends commonEntity {
	var res: String = ""

	def cond2QueryObj(): DBObject = {
		var o: DBObject = DBObject()
		conditions.getOrElse(Nil).foreach { x =>
			if (x.isQueryCond()) {
				o ++= x.cond2QueryDBObject
			}
		}
		o
	}


}
