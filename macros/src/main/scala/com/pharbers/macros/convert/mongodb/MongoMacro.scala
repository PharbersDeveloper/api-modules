package com.pharbers.macros.convert.mongodb

import com.pharbers.mongodb.dbtrait.DBTrait
import com.pharbers.mongodb.dbconnect.ConnectionInstance
import com.pharbers.mongodb.dbinstance.dbInstanceManager

class MongoMacro extends dbInstanceManager {
    override def instance(ci: ConnectionInstance): DBTrait = new mongoDBImpl(ci)
}