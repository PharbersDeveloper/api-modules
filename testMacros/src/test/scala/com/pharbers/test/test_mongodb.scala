package com.pharbers.test

import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.jsonapi.model._
import com.pharbers.macros.api.JsonapiConvert
import com.pharbers.macros.formJsonapi
import com.pharbers.util.log.phLogTrait
import com.pharbers.macros.convert.jsonapi.ResourceObjectReader
import com.pharbers.mongodb.{Conditions, eq_cond, request}

object test_mongodb extends App with CirceJsonapiSupport with phLogTrait {
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
	
	val entity = formJsonapi(jsonapi)(new RootReader())
//	println(entity)
	println(entity.cond2QueryObj())
	
	class RootReader() extends JsonapiConvert[request] with phLogTrait {
		
		import com.pharbers.jsonapi.model.RootObject._
		import com.pharbers.jsonapi.model._
		import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._

		import scala.reflect.runtime.{universe => ru}
		
		override def fromJsonapi(jsonapi: RootObject): request = {
			val entity_type = ru.typeOf[request]
			
			val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)
			
			// 解析attributes 到基础数据
			val jsonapi_data = jsonapi.data.get.asInstanceOf[ResourceObject]
			val entity = fromResourceObject[request](jsonapi_data)
			
			// 根据relationships 找到关联的includeds
			val relationships = jsonapi_data.relationships
			val includeds = jsonapi.included.get.resourceObjects.array
			val expandInfo = relationships.get.map{ case (k, v) =>
				k -> v.data.map(_.asInstanceOf[ResourceObjects]).get.array.map{x =>
					includeds.find(y => y.id == x.id && y.`type` == x.`type`)
						.getOrElse(throw new Exception(s"not found ${x.id}&${x.`type`} in includeds"))
				}
			}
			
			val conditions = expandInfo("conditions").map(fromResourceObject[eq_cond])
			entity.conditions = Some(conditions.toList)
			
			entity
		}
		
		override def toJsonapi(obj: request): RootObject = ???
	}
}