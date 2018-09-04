package com.pharbers.pattern.mongo

import com.pharbers.pattern.mongo.model.request
import com.pharbers.macros.convert.mongodb.MongoMacro

class mongoImpl() extends MongoMacro[request]

object mongoImpl {
    implicit def apply(): mongoImpl = new mongoImpl()
}
