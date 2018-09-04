package com.pharbers.mongodb.dbtrait

import com.mongodb.casbah.Imports._
import com.pharbers.mongodb.model.request

import scala.reflect.ClassTag

trait DBTrait {
	def queryObject[T: ClassTag](res: request): Option[T]
	def queryMultipleObject[T: ClassTag](res: request, sort : String = "date"): List[T]
	def insertObject[T: ClassTag](model: T): DBObject
	def updateObject[T: ClassTag](res: request): Int
	def deleteObject(res: request): Int
//	def queryCount
}
