package com.pharbers.test.mongo

import com.pharbers.util.log.phLogTrait
import com.pharbers.test.mongo.model.Person
import com.pharbers.jsonapi.model.RootObject
import com.pharbers.pattern.mongo.model.request
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport

object test_mongodb extends App with CirceJsonapiSupport with phLogTrait {

    import com.pharbers.macros._
    import com.pharbers.pattern.mongo.test_db_inst._
    import com.pharbers.macros.convert.jsonapi.JsonapiMacro._

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

    def findPerson: Option[Person] = {
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
              			  |			}
              			  |		}
              			  |	},
              			  |	"included": [{
              			  |		"id": "2",
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

    findOne
    findPerson

//    def queryMultipleObject = {
//        val jsonData =
//            """
//            |{
//            |	"data": {
//            |		"id": "1",
//            |		"type": "request",
//            |		"attributes": {
//            |			"res": "test2"
//            |		},
//            |		"relationships": {
//            |			"eq_conditions": {
//            |				"data": [{
//            |					"id": "2",
//            |					"type": "eq_cond"
//            |				}]
//            |			},
//            |         "fm_conditions": {
//            |				"data": {
//            |					"id": "3",
//            |					"type": "fm_cond"
//            |				}
//            |			}
//            |		}
//            |	},
//            |	"included": [{
//            |		"id": "2",
//            |		"type": "eq_cond",
//            |		"attributes": {
//            |			"key": "phone",
//            |			"value": "18510971868"
//            |		}
//            |	},{
//            |		"id": "3",
//            |		"type": "fm_cond",
//            |		"attributes": {
//            |			"skip": 2,
//            |			"take": 2
//            |		}
//            |	}]
//            |}
//            """.stripMargin
//
//        val json_data = parseJson(jsonData)
//        val jsonapi = decodeJson[RootObject](json_data)
//
//        val requests = formJsonapi[request](jsonapi)
//
//        val result = queryMultipleObject[Person](requests)
//        phLog(result)
//
//    }
//
//	def insertObject = {
//		val jsonData =
//			"""
//			  |{
//			  |	"data": {
//			  |		"id": "1",
//			  |		"type": "Consumers",
//			  |		"attributes": {
//			  |			"name": "Alex",
//			  |   		"age": 12,
//			  |     	"phone": "18510971868"
//			  |		},
//			  |		"relationships": {
//			  |			"orders": {
//			  |				"data": [{
//			  |					"id": "2",
//			  |					"type": "Order"
//			  |				}]
//			  |			}
//			  |		}
//			  |	},
//			  |	"included": [{
//			  |		"id": "2",
//			  |		"type": "Order",
//			  |		"attributes": {
//			  |			"title": "phone",
//			  |			"abc": 6400
//			  |		}
//			  |	}]
//			  |}
//			""".stripMargin
//
//		val json_data = parseJson(jsonData)
//		val jsonapi = decodeJson[RootObject](json_data)
//
//		val consumers = formJsonapi[Consumers](jsonapi)
//
//		val dbinstance = queryDBInstance("test").get
//		val result = dbinstance.insertObject[Consumers](consumers)
//		phLog(consumers.orders)
//		consumers.orders.get.foreach { x =>
//			dbinstance.insertObject[Order](x)
//		}
//		phLog(result)
//	}
//
//	def updataObject = {
//		val jsonData =
//			"""
//			  |{
//			  |	"data": {
//			  |		"id": "1",
//			  |		"type": "request",
//			  |		"attributes": {
//			  |			"res": "test"
//			  |		},
//			  |		"relationships": {
//			  |			"eq_conditions": {
//			  |				"data": [{
//			  |					"id": "2",
//			  |					"type": "eq_cond"
//			  |				}]
//			  |			},
//			  |   		"up_conditions": {
//			  |     		"data": [{
//			  |					"id": "3",
//			  |					"type": "up_cond"
//			  |				}]
//			  |     	}
//			  |		}
//			  |	},
//			  |	"included": [{
//			  |		"id": "2",
//			  |		"type": "eq_cond",
//			  |		"attributes": {
//			  |			"key": "phone",
//			  |			"value": "18994737968"
//			  |		}
//			  |	},{
//			  |		"id": "3",
//			  |		"type": "up_cond",
//			  |		"attributes": {
//			  |			"key": "phone",
//			  |			"value": "18510971868"
//			  |		}
//			  |	}]
//			  |}
//			""".stripMargin
//
//		val json_data = parseJson(jsonData)
//		val jsonapi = decodeJson[RootObject](json_data)
//
//		val requests = formJsonapi[request](jsonapi)
//
//		val result = queryDBInstance("test").get.updateObject[test](requests)
//		phLog(result)
//	}
//
//	def deleteObject = {
//		val jsonData =
//			"""
//			  |{
//			  |	"data": {
//			  |		"id": "1",
//			  |		"type": "request",
//			  |		"attributes": {
//			  |			"res": "test"
//			  |		},
//			  |		"relationships": {
//			  |			"eq_conditions": {
//			  |				"data": [{
//			  |					"id": "2",
//			  |					"type": "eq_cond"
//			  |				}]
//			  |			}
//			  |		}
//			  |	},
//			  |	"included": [{
//			  |		"id": "2",
//			  |		"type": "eq_cond",
//			  |		"attributes": {
//			  |			"key": "phone",
//			  |			"value": "0000"
//			  |		}
//			  |	}]
//			  |}
//			""".stripMargin
//
//		val json_data = parseJson(jsonData)
//		val jsonapi = decodeJson[RootObject](json_data)
//
//		val requests = formJsonapi[request](jsonapi)
//		val result = queryDBInstance("test").get.deleteObject(requests)
//		phLog(result)
//	}
//
//	def updataObject2 = {
//		val jsonData =
//			"""
//			  |{
//			  |    "data": {
//			  |        "id": "1",
//			  |        "type": "request",
//			  |        "attributes": {
//			  |            "res": "test"
//			  |        },
//			  |        "relationships": {
//			  |            "eq_conditions": {
//			  |                "data": [
//			  |                    {
//			  |                        "id": "2",
//			  |                        "type": "eq_cond"
//			  |                    }
//			  |                ]
//			  |            },
//			  |            "up_conditions": {
//			  |                "data": [
//			  |                    {
//			  |                        "id": "3",
//			  |                        "type": "up_cond"
//			  |                    }
//			  |                ]
//			  |            }
//			  |        }
//			  |    },
//			  |    "included": [
//			  |        {
//			  |            "id": "2",
//			  |            "type": "eq_cond",
//			  |            "attributes": {
//			  |                "key": "name",
//			  |                "value": "sdad"
//			  |            }
//			  |        },
//			  |        {
//			  |            "id": "3",
//			  |            "type": "up_cond",
//			  |            "attributes": {
//			  |                "key": "phone",
//			  |                "value": [
//			  |                  {"name": "a"},{"name": "l"}
//			  |                ]
//			  |            }
//			  |        }
//			  |    ]
//			  |}
//			""".stripMargin
//
//		val json_data = parseJson(jsonData)
//		val jsonapi = decodeJson[RootObject](json_data)
//
//		val requests = formJsonapi[request](jsonapi)
//
//		val result = queryDBInstance("test").get.updateObject[test](requests)
//		phLog(result)
//	}


    //	findOne
    //	findPerson
    //	queryMultipleObject
    //	insertObject
    //	updataObject
    //	updataObject2
    //	deleteObjec
}
