package com.pharbers.test

import com.pharbers.model.profile
import com.pharbers.jsonapi.model._
import com.pharbers.util.log.phLogTrait
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.jsonapi.model.RootObject.ResourceObject
import com.pharbers.macros.api.JsonapiConvert
import com.pharbers.macros.convert.jsonapi.ResourceObjectReader
import com.pharbers.test.test_jsonapi.entity

object test_resources_object extends App with CirceJsonapiSupport with phLogTrait with JsonapiConvert[profile] {
	val test_data =
		"""
          {
 | "data": {
 |  "id": "01",
 |  "type": "Contact",
 |  "attributes": {
 |   "name": "jeorch",
 |   "age" : 18
 |  },
 |  "relationships": {
 |      "orders": {
 |       "data": [{
 |        "id": "3",
 |        "type": "Order"
 |       },{
 |        "id": "4",
 |        "type": "Order"
 |       }]
 |      }
 |  }
 | },
 | "included":[
 |  {
 |   "id": "3",
 |   "type": "Order",
 |   "attributes": {
 |       "title": "蟠桃"
 |   }
 |  },
 |  {
 |   "id": "4",
 |   "type": "Order",
 |   "attributes": {
 |       "title": "香蕉"
 |   }
 |  }
 | ]
 |}
        """.stripMargin
	val json_data = parseJson(test_data)
	val jsonapi = decodeJson[RootObject](json_data)
	//    phLog(jsonapi)
	
	val resources = jsonapi.data.get.asInstanceOf[ResourceObject]
	phLog(resources)
	
	//    val entity = fromResourceObject(resources)(profile())(new resourceReader())
	import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._
	
	val entity = fromResourceObject[profile](resources)
	println(entity.id)
	println(entity.`type`)
	println(entity.name)
	println(entity.age)
	println(entity.company)
	println(entity.orders)
	
	val result = toResourceObject(entity)
	println(result)
	

class resourceReader() extends ResourceObjectReader[profile] {
	
	import com.pharbers.jsonapi.model._
	import scala.reflect.runtime.{universe => ru}
	import com.pharbers.jsonapi.model.RootObject._
	import com.pharbers.jsonapi.model.JsonApiObject._
	
	override def fromResourceObject(resource: ResourceObject): profile = {
		val entity_type = ru.typeOf[profile]
		val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)
		val class_symbol = entity_type.typeSymbol.asClass
		val class_mirror = runtime_mirror.reflectClass(class_symbol)
		val ctor_symbol = entity_type.decl(ru.termNames.CONSTRUCTOR).asMethod
		val ctor_mirror = class_mirror.reflectConstructor(ctor_symbol)
		val entity = ctor_mirror().asInstanceOf[profile]
		
		entity.id = resource.id.get
		entity.`type` = resource.`type`
		
		val attrs = resource.attributes.get.toList
		val inst_mirror = runtime_mirror.reflect(entity)
		attrs.foreach { attr =>
			val field_symbol = entity_type.decl(ru.TermName(attr.name)).asTerm
			val field_mirror = inst_mirror.reflectField(field_symbol)
			attr.value match {
				case StringValue(str) => field_mirror.set(str)
				case NumberValue(number) if number.isValidInt => field_mirror.set(number.toInt)
				case NumberValue(number) if number.isBinaryDouble => field_mirror.set(number.toDouble)
				case NumberValue(number) if number.isValidLong => field_mirror.set(number.toLong)
				case NumberValue(_) => ???
				case BooleanValue(number) => field_mirror.set(number)
				case NullValue => ???
				case _ => ???
			}
		}
		
		entity
	}
	
	override def toResourceObject(obj: profile): ResourceObject = {
		val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)
		val inst_mirror = runtime_mirror.reflect(obj)
		val class_symbol = inst_mirror.symbol
		val class_field = class_symbol.typeSignature.members.filter(p => p.isTerm && !p.isMethod).toList
		
		val companion_symbol = class_symbol.companion.asModule
		val companion_mirror = runtime_mirror.reflectModule(companion_symbol)
		val companion_instance = runtime_mirror.reflect(companion_mirror.instance)
		
		def isConnOneInject(f: ru.Symbol): Boolean =
			f.info.baseType(ru.typeOf[Option[_]].typeSymbol) != ru.NoType &&
				f.info.typeArgs.length == 1 &&
				f.info.typeArgs.head.baseClasses.
					map(_.name.toString).contains("commonEntity")
		
		def isConnManyInject(f: ru.Symbol): Boolean =
			f.info.baseType(ru.typeOf[Option[List[_]]].typeSymbol) != ru.NoType &&
				f.info.typeArgs.length == 1 &&
				f.info.typeArgs.head.baseClasses.
					map(_.name.toString).contains("commonEntity")
		
		val attrs = class_field.map { f =>
			val attr_mirror = inst_mirror.reflectField(f.asTerm)
			val attr_val = attr_mirror.get
			
			Attribute(f.name.toString,
				if (f.info =:= ru.typeOf[String]) StringValue(attr_val.toString)
				else if (f.info <:< ru.typeOf[AnyVal]) NumberValue(BigDecimal(attr_val.asInstanceOf[Number].doubleValue))
				else if (f.info =:= ru.typeOf[Boolean]) BooleanValue(attr_val.asInstanceOf[Boolean])
				else NullValue
			)
		}.filterNot(NullValue == _.value).asInstanceOf[Attributes]
		
		ResourceObject(
			id = Some(obj.id),
			`type` = obj.`type`,
			attributes = Some(
				attrs.toList
			))
	}
}

override def fromJsonapi(jsonapi: RootObject) = ???
override def toJsonapi(obj: profile) = ???
}
