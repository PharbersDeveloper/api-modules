package com.pharbers.pattern.mongo.model

import com.mongodb.casbah.Imports._
import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._
import com.pharbers.macros.convert.mongodb.TraitRequest

@One2ManyConn[eq_cond]("eq_conditions")
@One2ManyConn[up_cond]("up_conditions")
@One2OneConn[fm_cond]("fm_conditions")
@ToStringMacro
class request extends commonEntity with TraitRequest {
    override var res: String = ""

    override def cond2QueryObj(): DBObject = {
        var o: DBObject = DBObject()
        eq_conditions.getOrElse(Nil).foreach { x =>
            if (x.isQueryCond) {
                o ++= x.cond2QueryDBObject()
            }
        }
        o
    }

    override def cond2UpdateObj(): DBObject = {
        var o: DBObject = DBObject()
        up_conditions.getOrElse(Nil).foreach { x =>
            if (x.isUpdateCond) {
                o ++= x.cond2UpdateDBObectj()
            }
        }
        o
    }

    override def cond2fmQueryObj(): (Int, Int) = (fm_conditions.get.skip, fm_conditions.get.take)

}
