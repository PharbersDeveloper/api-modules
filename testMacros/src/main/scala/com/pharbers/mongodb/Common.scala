package com.pharbers.mongodb

import com.mongodb.casbah.MongoClient

trait Common {
	def queryObject[T](res: request, model: T) = {
		val mongoClient = MongoClient("localhost", 27017)
		println("fuck")
	}
}
