package com.pharbers.test

import com.pharbers.macros._
import com.pharbers.model.{company, profile}
import com.pharbers.jsonapi.model._
import com.pharbers.util.log.phLogTrait
//import com.pharbers.macros.convert.JsonapiMacro._
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.macros.api.JsonapiConvert

object test_jsonapi extends App with CirceJsonapiSupport with phLogTrait {
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

    implicit val impl = new convert()
    val entity = formJsonapi[profile](jsonapi)
    println(entity)
    println(entity.id + entity.`type` + entity.major + entity.minor)

    class convert() extends JsonapiConvert[profile] with phLogTrait {

        import com.pharbers.jsonapi.model._
        import scala.reflect.runtime.{universe => ru}
        import com.pharbers.jsonapi.model.JsonApiObject._
        import com.pharbers.jsonapi.model.RootObject.ResourceObject

        override def fromJsonapi(jsonapi: RootObject): profile = {
            val entity = profile()
            val sub = company()

            val obj_type = ru.typeOf[profile]

            val runtime_mirror = ru.runtimeMirror(entity.getClass.getClassLoader)
            val inst_mirror = runtime_mirror.reflect(entity)

            val jsonapi_data = jsonapi.data.map(_.asInstanceOf[ResourceObject]).get
            entity.id = jsonapi_data.id.get
            entity.`type` = jsonapi_data.`type`

            val attrs = jsonapi_data.attributes.get.toList
            attrs.foreach { attr =>
                val field_symbol = obj_type.decl(ru.TermName(attr.name)).asTerm
                attr.value match {
                    case StringValue(str) => inst_mirror.reflectField(field_symbol).set(str)
                    case NumberValue(number) if number.isValidInt => inst_mirror.reflectField(field_symbol).set(number.toInt)
                    case NumberValue(number) if number.isBinaryDouble => inst_mirror.reflectField(field_symbol).set(number.toDouble)
                    case NumberValue(number) if number.isValidLong => inst_mirror.reflectField(field_symbol).set(number.toLong)
                    case NumberValue(_) => ???
                    case BooleanValue(number) => inst_mirror.reflectField(field_symbol).set(number)
                    case NullValue => ???
                    case _ => ???
                }
            }

            val includeds = jsonapi.included.get.resourceObjects.array
            includeds.head.id

            includeds.foreach(println)

            val relationships = jsonapi_data.relationships.get
            relationships.map{ case (str, relp) =>
                println(str)
                val obj = relp.data.get
                println(obj)
                (str, relp)
            }

            entity
        }

        override def toJsonapi(obj: profile): RootObject = ???

    }


//
//            val u = profile("ppp")
//            val u2 = profile("dddd")
//            val u3 = profile("cccc")
//            val d = company("ppp")
//    println(c.id)
//    println(c.company_name)
//    println(c.users)
//    c.users = Some(u :: Nil)
//    c.users = Some(u2 :: c.users.get)
////    c.users = d +: c.users
//    println(c.users)
//
//    println(c.user)
//    c.user = Some(u2)
//    println(c.user)
//        c.user = Some(u3)
//        println(c.user)
//    println(c.id)
//    val u = user()
//    import u._
//    println(user().toString())
//    var a = List(1,2 ,3)
//    4 +: a

//    println(a)
//    println(entity.id)
//    println(entity.major)
//    println(entity.minor)

}
