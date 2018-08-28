package com.pharbers

import com.mongodb.DBObject
import com.pharbers.jsonapi.model.RootObject
import com.pharbers.macros.api.{JsonapiConvert, MongoDBConvert}

package object macros {

    def formJsonapi[T: JsonapiConvert](jsonapi: RootObject): T =
        implicitly[JsonapiConvert[T]].fromJsonapi(jsonapi)
    def toJsonapi[T: JsonapiConvert](obj: T) : RootObject =
        implicitly[JsonapiConvert[T]].toJsonapi(obj)

    def fromMongoDB[T: MongoDBConvert](mongo: DBObject): T =
        implicitly[MongoDBConvert[T]].fromMongo(mongo)
    def toMongoDB[T: MongoDBConvert](obj: T): DBObject =
        implicitly[MongoDBConvert[T]].toMongo(obj)

}
