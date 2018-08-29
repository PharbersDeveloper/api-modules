package com.pharbers.test

import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.jsonapi.model._
import com.pharbers.macros._
import com.pharbers.util.log.phLogTrait
import com.pharbers.mongodb._

object test_mongodb extends App with CirceJsonapiSupport with phLogTrait with Common {
	val jsonData =
		"""
		  |{
		  |	"data": {
		  |		"id": "1",
		  |		"type": "request",
		  |		"attributes": {
		  |			"res": "BMPhone"
		  |		},
		  |		"relationships": {
		  |			"conditions": {
		  |				"data": [{
		  |					"id": "2",
		  |					"type": "eq_cond"
		  |				}, {
		  |					"id": "3",
		  |					"type": "lt_cond"
		  |				}]
		  |			}
		  |		}
		  |	},
		  |	"included": [{
		  |		"id": "2",
		  |		"type": "eq_cond",
		  |		"attributes": {
		  |			"key": "phone",
		  |			"value": "13720200891"
		  |		}
		  |	},{
		  |		"id": "3",
		  |		"type": "lt_cond",
		  |		"attributes": {
		  |			"key": "name",
		  |			"value": "Alex"
		  |		}
		  |	}]
		  |}
		""".stripMargin
	
	val json_data = parseJson(jsonData)
	val jsonapi = decodeJson[RootObject](json_data)
	
	import com.pharbers.macros.convert.jsonapi.JsonapiMacro._
	val entity = formJsonapi[request](jsonapi)(jsonapiMacroMaterialize)
	phLog(entity.cond2QueryObj())
	
	queryObject(entity, null)
}
