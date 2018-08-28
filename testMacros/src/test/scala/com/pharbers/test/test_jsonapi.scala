package com.pharbers.test

import com.pharbers.macros._
import com.pharbers.jsonapi.model._
import com.pharbers.util.log.phLogTrait
import com.pharbers.macros.api.JsonapiConvert
import com.pharbers.model.{Order, company, profile}
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.macros.convert.jsonapi.ResourceObjectReader

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
    phLog(jsonapi)

    val entity = formJsonapi(jsonapi)(new RootReader())
    println(entity)

//    val result = toJsonapi(entity)(new RootReader())
//    println(result)


    class RootReader() extends JsonapiConvert[profile] with phLogTrait {

        import com.pharbers.jsonapi.model._
        import scala.reflect.runtime.{universe => ru}
        import com.pharbers.jsonapi.model.RootObject._
        import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._

        override def fromJsonapi(jsonapi: RootObject): profile = {
            val entity_type = ru.typeOf[profile]
//            implicit val resoObjReader: ResourceObjectReader[profile] = ResourceReaderMaterialize[profile]

            val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)

            // 解析attributes 到基础数据
            val jsonapi_data = jsonapi.data.get.asInstanceOf[ResourceObject]
            val entity = fromResourceObject[profile](jsonapi_data)

            // 根据relationships 找到关联的includeds
            val relationships = jsonapi_data.relationships
            val includeds = jsonapi.included.get.resourceObjects.array
            val expandInfo = relationships.get.map{ case (k, v) =>
                k -> v.data.map(_.asInstanceOf[ResourceObjects]).get.array.map{x =>
                    includeds.find(y => y.id == x.id && y.`type` == x.`type`)
                            .getOrElse(throw new Exception(s"not found ${x.id}&${x.`type`} in includeds"))
                }
            }
            println(expandInfo)

            val orders = expandInfo("orders").map(fromResourceObject[Order])
            entity.orders = Some(orders.toList)

//            val m = ru.runtimeMirror(getClass.getClassLoader)
//            val csymbol = m.classSymbol(Class.forName("ATest"))
//            println(csymbol)
//            val cm = m.reflectClass(csymbol)
//            val constructors = csymbol.typeSignature.members.filter(_.isConstructor).toList
//            val constructorMirror = cm.reflectConstructor(constructors.head.asMethod) // we can reuse it
//            val p = constructorMirror("alfred")
//            println(p.getClass)
//            println(p)


//            val inst_mirror = runtime_mirror.reflect(entity)
//            val class_symbol = inst_mirror.symbol
//            val fields = class_symbol.typeSignature.members.filter(p => p.isTerm && !p.isMethod).toList


            entity
        }

        override def toJsonapi(obj: profile): RootObject = ???

    }

}
