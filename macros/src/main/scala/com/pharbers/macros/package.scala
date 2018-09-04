package com.pharbers

import com.mongodb.DBObject
import scala.reflect.ClassTag
import com.pharbers.mongodb.dbtrait.DBTrait
import com.pharbers.jsonapi.model.RootObject

package object macros {
    type JsonapiConvert[T] = com.pharbers.macros.api.JsonapiConvert[T]

    def formJsonapi[T: JsonapiConvert](jsonapi: RootObject): T =
        implicitly[JsonapiConvert[T]].fromJsonapi(jsonapi)

    def toJsonapi[T: JsonapiConvert](obj: T): RootObject =
        implicitly[JsonapiConvert[T]].toJsonapi(obj)

    def toJsonapi[T: JsonapiConvert](objLst: List[T]): RootObject =
        implicitly[JsonapiConvert[T]].toJsonapi(objLst)

    def queryObject[T, R](res: R)(implicit dbt: DBTrait[R]): Option[T] = dbt.queryObject(res)

    def queryMultipleObject[T, R](res: R, sort: String = "date")(implicit dbt: DBTrait[R]): List[T] = dbt.queryMultipleObject(res, sort)

    def insertObject[T: ClassTag, R](model: T)(implicit dbt: DBTrait[R]): DBObject = dbt.insertObject(model)

    def updateObject[T: ClassTag, R](res: R)(implicit dbt: DBTrait[R]): Int = dbt.updateObject(res)

    def deleteObject[R](res: R)(implicit dbt: DBTrait[R]): Int = dbt.deleteObject(res)

    def queryCount[R](implicit dbt: DBTrait[R]): Long = dbt.queryCount

}
