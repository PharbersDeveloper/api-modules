package com.pharbers.macros.convert.mongodb

import com.pharbers.mongodb.dbtrait.DBTrait
import com.pharbers.model222.request
import com.pharbers.mongodb.dbconnect.ConnectionInstance
import com.pharbers.mongodb.dbinstance.dbInstanceManager

class MongoMacro extends dbInstanceManager[request] {
    override def instance(ci: ConnectionInstance): DBTrait[request] = new mongoDBImpl(ci)
}