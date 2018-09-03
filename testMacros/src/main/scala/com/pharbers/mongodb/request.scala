package com.pharbers.mongodb

import com.mongodb.casbah.Imports._
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.{One2ManyConn, ToStringMacro}

@One2ManyConn[eq_cond]("eq_conditions")
@One2ManyConn[up_cond]("up_conditions")
@ToStringMacro
case class request() extends commonEntity {
	var res: String = ""

	def cond2QueryObj(): DBObject = {
		var o: DBObject = DBObject()
		eq_conditions.getOrElse(Nil).foreach { x =>
			if (x.isQueryCond()) {
				o ++= x.cond2QueryDBObject()
			}
		}
		o
	}

	def cond2UpdateObj(): DBObject = {
		var o: DBObject = DBObject()
		up_conditions.getOrElse(Nil).foreach { x =>
			if (x.isUpdateCond()) {
				o ++= x.cond2UpdateDBObectj()
			}
		}
		o
	}

}
