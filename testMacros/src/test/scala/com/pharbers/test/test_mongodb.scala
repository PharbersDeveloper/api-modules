package com.pharbers.test

import com.mongodb.DBObject
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.jsonapi.model.RootObject
import com.pharbers.pattern.mongo.model.request
import com.pharbers.test.mongo.model.{Consumers, Order, Person}
import com.pharbers.util.log.phLogTrait

object test_mongodb extends App with CirceJsonapiSupport with phLogTrait {
	
	import com.pharbers.macros._
	import com.pharbers.macros.convert.jsonapi.JsonapiMacro._
	import com.pharbers.pattern.mongo.test_db_inst._
	
	def findOne: Option[Person] = {
		val jsonData =
			"""
			  			  |{
			  			  |	"data": {
			  			  |		"id": "1",
			  			  |		"type": "request",
			  			  |		"attributes": {
			  			  |			"res": "test"
			  			  |		},
			  			  |		"relationships": {
			  			  |			"eq_conditions": {
			  			  |				"data": [{
			  			  |					"id": "2",
			  			  |					"type": "eq_cond"
			  			  |				}, {
			  			  |					"id": "3",
			  			  |					"type": "eq_cond"
			  			  |				}]
			  			  |			}
			  			  |		}
			  			  |	},
			  			  |	"included": [{
			  			  |		"id": "2",
			  			  |		"type": "eq_cond",
			  			  |		"attributes": {
			  			  |			"key": "phone",
			  			  |			"value": "18510971868"
			  			  |		}
			  			  |	},{
			  			  |		"id": "3",
			  			  |		"type": "eq_cond",
			  			  |		"attributes": {
			  			  |			"key": "name",
			  			  |			"value": "Alex"
			  			  |		}
			  			  |	}]
			  			  |}
			""".stripMargin
		val json_data = parseJson(jsonData)
		val jsonapi = decodeJson[RootObject](json_data)
		
		val requests = formJsonapi[request](jsonapi)
		println(requests)
		
		val result = queryObject[Person](requests)
		println(result)
		result
	}
	
	//    findOne
	def queryMulti: List[Person] = {
		val jsonData =
			"""
			  |{
			  |	"data": {
			  |		"id": "1",
			  |		"type": "request",
			  |		"attributes": {
			  |			"res": "test2"
			  |		},
			  |		"relationships": {
			  |			"eq_conditions": {
			  |				"data": [{
			  |					"id": "2",
			  |					"type": "eq_cond"
			  |				}]
			  |			},
			  |         "fm_conditions": {
			  |				"data": {
			  |					"id": "3",
			  |					"type": "fm_cond"
			  |				}
			  |			}
			  |		}
			  |	},
			  |	"included": [{
			  |		"id": "2",
			  |		"type": "eq_cond",
			  |		"attributes": {
			  |			"key": "phone",
			  |			"value": "18510971868"
			  |		}
			  |	},{
			  |		"id": "3",
			  |		"type": "fm_cond",
			  |		"attributes": {
			  |			"skip": 0,
			  |			"take": 2
			  |		}
			  |	}]
			  |}
			""".stripMargin
		val json_data = parseJson(jsonData)
		val jsonapi = decodeJson[RootObject](json_data)
		
		val requests = formJsonapi[request](jsonapi)
		println(requests)
		
		val result = queryMultipleObject[Person](requests)
		println(result)
		result
	}
	
	//    queryMulti
	def insert: DBObject = {
		val jsonData =
			"""
			  |{
			  |	"data": {
			  |		"id": "1",
			  |		"type": "Consumers",
			  |		"attributes": {
			  |			"name": "Alex",
			  |   		"age": 12,
			  |     	"phone": "18510971868"
			  |		},
			  |		"relationships": {
			  |			"orders": {
			  |				"data": [{
			  |					"id": "2",
			  |					"type": "Order"
			  |				}]
			  |			}
			  |		}
			  |	},
			  |	"included": [{
			  |		"id": "2",
			  |		"type": "Order",
			  |		"attributes": {
			  |			"title": "phone",
			  |			"abc": 6400
			  |		}
			  |	}]
			  |}
			""".stripMargin
		val json_data = parseJson(jsonData)
		val jsonapi = decodeJson[RootObject](json_data)
		
		val consumers = formJsonapi[Consumers](jsonapi)
		println(consumers)
		
		val result = insertObject[Consumers](consumers)
		println(consumers.orders)
		consumers.orders.get.foreach { x =>
			insertObject[Order](x)
		}
		println(result)
		result
	}
	
	//	insert
	def updata: Int = {
		val jsonData =
			"""
			  |{
			  |	"data": {
			  |		"id": "1",
			  |		"type": "request",
			  |		"attributes": {
			  |			"res": "Person"
			  |		},
			  |		"relationships": {
			  |			"eq_conditions": {
			  |				"data": [{
			  |					"id": "2",
			  |					"type": "eq_cond"
			  |				}]
			  |			},
			  |   		"up_conditions": {
			  |     		"data": [{
			  |					"id": "3",
			  |					"type": "up_cond"
			  |				}]
			  |     	}
			  |		}
			  |	},
			  |	"included": [{
			  |		"id": "2",
			  |		"type": "eq_cond",
			  |		"attributes": {
			  |			"key": "name",
			  |			"value": "Alex"
			  |		}
			  |	},{
			  |		"id": "3",
			  |		"type": "up_cond",
			  |		"attributes": {
			  |			"key": "name",
			  |			"value": "Alex2"
			  |		}
			  |	}]
			  |}
			""".stripMargin
		val json_data = parseJson(jsonData)
		val jsonapi = decodeJson[RootObject](json_data)
		
		val requests = formJsonapi[request](jsonapi)
		println(requests)
		
		val result = updateObject[Person](requests)
		println(result)
		result
	}
//	updata
	
	def delete = {
		val jsonData =
			"""
			  |{
			  |	"data": {
			  |		"id": "1",
			  |		"type": "request",
			  |		"attributes": {
			  |			"res": "test"
			  |		},
			  |		"relationships": {
			  |			"eq_conditions": {
			  |				"data": [{
			  |					"id": "2",
			  |					"type": "eq_cond"
			  |				}]
			  |			}
			  |		}
			  |	},
			  |	"included": [{
			  |		"id": "2",
			  |		"type": "eq_cond",
			  |		"attributes": {
			  |			"key": "phone",
			  |			"value": "0000"
			  |		}
			  |	}]
			  |}
			""".stripMargin
		val json_data = parseJson(jsonData)
		val jsonapi = decodeJson[RootObject](json_data)
		
		val requests = formJsonapi[request](jsonapi)
		println(requests)
		
		val result = deleteObject(requests)
		phLog(result)
	}
//	delete
	
}
