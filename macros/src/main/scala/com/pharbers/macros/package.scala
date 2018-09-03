package com.pharbers

import com.mongodb.DBObject
import com.pharbers.jsonapi.model.RootObject

package object macros {
    type JsonapiConvert[T] = com.pharbers.macros.api.JsonapiConvert[T]
    type MongoDBConvert[T] = com.pharbers.macros.api.MongoDBConvert[T]

    def formJsonapi[T: JsonapiConvert](jsonapi: RootObject): T =
        implicitly[JsonapiConvert[T]].fromJsonapi(jsonapi)
    def toJsonapi[T: JsonapiConvert](obj: T) : RootObject =
        implicitly[JsonapiConvert[T]].toJsonapi(obj)
    def toJsonapi[T: JsonapiConvert](objLst: List[T]) : RootObject =
        implicitly[JsonapiConvert[T]].toJsonapi(objLst)


    def fromMongoDB[T: MongoDBConvert](mongo: DBObject): T =
        implicitly[MongoDBConvert[T]].fromMongo(mongo)
    def toMongoDB[T: MongoDBConvert](obj: T): DBObject =
        implicitly[MongoDBConvert[T]].toMongo(obj)

}
