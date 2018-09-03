import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.jsonapi.model._
import com.pharbers.macros._
import com.pharbers.model.{Consumers, Person, test}
import com.pharbers.util.log.phLogTrait
import com.pharbers.mongodb._
import com.pharbers.macros.convert.jsonapi.JsonapiMacro._


object test_mongodb extends App with CirceJsonapiSupport with phLogTrait with Common {

	def findOne = {
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
			  |			"conditions": {
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
		phLog(requests.cond2QueryObj())

		val result = queryObject[Person](requests)
		println(result)
	}

	def find = {
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
			  |			"conditions": {
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
			  |			"value": "18510971868"
			  |		}
			  |	}]
			  |}
			""".stripMargin

		val json_data = parseJson(jsonData)
		val jsonapi = decodeJson[RootObject](json_data)

		val requests = formJsonapi[request](jsonapi)
		phLog(requests.cond2QueryObj())

		val result = queryMultipleObject[Person](requests)
		result.foreach(a => println(a.name))
	}

	def insert = {
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

		insertObject[Consumers](consumers)
//		consumers.orders.get.foreach { x =>
//			insertObject[Order](x)
//		}
		phLog(consumers)
	}

	def updata = {
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
			  |			"key": "phone",
			  |			"value": "18510971868"
			  |		}
			  |	},{
			  |		"id": "3",
			  |		"type": "up_cond",
			  |		"attributes": {
			  |			"key": "phone",
			  |			"value": "18510971868"
			  |		}
			  |	}]
			  |}
			""".stripMargin

		val json_data = parseJson(jsonData)
		val jsonapi = decodeJson[RootObject](json_data)

		val requests = formJsonapi[request](jsonapi)

		updateObject[test](requests)

	}

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
		deleteObject(requests)
	}

//	delete
//	updata
//	insert
//	find
//	findOne
}
