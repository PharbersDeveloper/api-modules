package com.pharbers.macros.api

import com.mongodb.DBObject

trait MongoDBConvert[T] {
    def fromMongo(mongo: DBObject): T
    def toMongo(obj: T): DBObject
}