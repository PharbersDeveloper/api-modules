package com.pharbers.pattern.mongo

import com.pharbers.pattern.mongo.model.request
import com.pharbers.macros.convert.mongodb.{MongoMacro, TraitRequest}
import com.pharbers.mongodb.dbtrait.DBTrait

class mongoImpl() extends MongoMacro[request]

object test_db_inst {
    implicit def db_inst: DBTrait[TraitRequest] = new mongoImpl().queryDBInstance("test").get.asInstanceOf[DBTrait[TraitRequest]]
}
