package com.pharbers.mongodb

import com.mongodb.casbah.MongoClient

trait Common {
	def queryObject[T](res: request, model: T) = {
		val mongoClient = MongoClient("localhost", 27017)
		val db = mongoClient("Test")
		val coll = db("test")
		val conditions = res.cond2QueryObj()
		val result = coll.findOne(conditions)
		println(result)
	}
}
