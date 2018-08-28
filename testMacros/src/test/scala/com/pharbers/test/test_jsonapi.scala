package com.pharbers.test

import com.pharbers.macros._
import com.pharbers.jsonapi.model._
import com.pharbers.util.log.phLogTrait

import scala.reflect.runtime.universe._
import com.pharbers.macros.api.JsonapiConvert
import com.pharbers.model.{Order, abc, company, profile}
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.sun.tools.javac.code.TypeTag

import scala.reflect.ClassTag

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
 |      "abc": {
 |          "data" : {
 |              "id": "2",
 |              "type": "company"
 |          }
 |      },
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
 |   "id": "2",
 |   "type": "company",
 |   "attributes": {
 |       "company_name": "法伯科技"
 |   }
 |  },
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

//    val entity0 = formJsonapi(jsonapi)(new RootReader())
//    phLog(entity0)

    def a[T](a: T)(implicit ct: ClassTag[T]) = {

        println(a.isInstanceOf[ct.class])
    }

    a[Int](1)

//    import com.pharbers.macros.convert.jsonapi.JsonapiMacro._
//    val entity1 = formJsonapi[profile](jsonapi)
//    phLog(entity1)

//    val result = toJsonapi(entity)(new RootReader())
//    println(result)



//    class RootReader() extends JsonapiConvert[profile] with phLogTrait {
//
//        import com.pharbers.jsonapi.model._
//        import scala.reflect.runtime.{universe => ru}
//        import com.pharbers.jsonapi.model.RootObject._
//        import com.pharbers.jsonapi.model.JsonApiObject._
//        import com.pharbers.macros.convert.jsonapi.ResourceObjectReader
//        import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._
//        import ru._
//
//        def extract_abc(rd: Option[RootObject.Data]): Option[abc] = {
//            rd match {
//                case Some(reo: ResourceObject) => Some(fromResourceObject[abc](reo))
//                case _ => None
//            }
//        }
//
//        def extract_Order(rd: Option[RootObject.Data]): Option[List[Order]] = {
//            rd match {
//                case Some(reos: ResourceObjects) => Some(reos.array.map(fromResourceObject[Order]).toList)
//                case _ => None
//            }
//        }
//
//        override def fromJsonapi(jsonapi: RootObject, package_local: String): profile = {
//            val entity_type = ru.typeOf[profile]
//            val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)
//
//            /** 解析attributes 到基础数据 **/
//            val jsonapi_data = jsonapi.data.get.asInstanceOf[ResourceObject]
//            val entity = fromResourceObject[profile](jsonapi_data)(ResourceReaderMaterialize)
////            phLog("entity in fromJsonapi is ===> " + entity)
//
//            /** 根据relationships 找到关联的includeds **/
//            val relationships = jsonapi_data.relationships
//            val includeds = jsonapi.included.get.resourceObjects.array
//            val expandInfo = relationships.get.map { case (k, v) =>
//                val tmp = v.data match {
//                    case Some(reo: ResourceObject) =>
//                        Some(includeds.find(y => y.id == reo.id && y.`type` == reo.`type`)
//                                .getOrElse(throw new Exception(s"not found ${reo.id}&${reo.`type`} in includeds")))
//                    case Some(reos: ResourceObjects) =>
//                        Some(ResourceObjects(reos.array.map { reo =>
//                            includeds.find(y => y.id == reo.id && y.`type` == reo.`type`)
//                                    .getOrElse(throw new Exception(s"not found ${reo.id}&${reo.`type`} in includeds"))
//                        }))
//                    case None => None
//                }
//                k -> tmp
//            }
////            phLog("expandInfo in fromJsonapi is ===> " + expandInfo)
//
//            /** 将展开的 relationships 赋值到实体中 **/
//            val inst_mirror = runtime_mirror.reflect(entity)
//            expandInfo.foreach { case (k, v) =>
//                val field_type = entity_type.member(ru.TermName(k))
//                val field_mirror = inst_mirror.reflectField(field_type.asTerm)
//                k match {
//                    case "abc" => field_mirror.set(extract_abc(v))
//                    case "orders" => field_mirror.set(extract_Order(v))
//                }
//            }
//            entity
//        }
//
//        override def toJsonapi(obj: profile): RootObject = ???
//
//    }
}