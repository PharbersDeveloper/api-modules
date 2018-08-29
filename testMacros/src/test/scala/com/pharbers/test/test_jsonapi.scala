package com.pharbers.test

import com.pharbers.macros._
import com.pharbers.model.profile
import com.pharbers.jsonapi.model._
import com.pharbers.util.log.phLogTrait
import com.pharbers.macros.convert.jsonapi.JsonapiMacro._
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport

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
 |      "company": {
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

//    val entity = formJsonapi(jsonapi)(new RootReader())
//    phLog(entity)

    val entity = formJsonapi[profile](jsonapi)
    phLog(entity)

    val result = toJsonapi(entity)(new RootReader())
    phLog(result)


    class RootReader() extends JsonapiConvert[profile] with phLogTrait {

        import com.pharbers.jsonapi.model._
        import com.pharbers.macros.convert.jsonapi._
        import scala.reflect.runtime.{universe => ru}
        import com.pharbers.jsonapi.model.RootObject._
        import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._

        override def fromJsonapi(jsonapi: RootObject, package_local: String): profile = {
            val entity_type = ru.typeOf[profile]
            val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)

            /** 解析attributes 到基础数据 **/
            val jsonapi_data = jsonapi.data.get.asInstanceOf[ResourceObject]
            val entity = fromResourceObject[profile](jsonapi_data)(ResourceReaderMaterialize)
//            phLog("entity in fromJsonapi is ===> " + entity)

            /** 根据relationships 找到关联的includeds **/
            val relationships = jsonapi_data.relationships
            val includeds = jsonapi.included.get.resourceObjects.array
            val expandInfo = relationships.get.map { case (k, v) =>
                val tmp = v.data match {
                    case Some(reo: ResourceObject) =>
                        Some(includeds.find(y => y.id == reo.id && y.`type` == reo.`type`)
                                .getOrElse(throw new Exception(s"not found ${reo.id}&${reo.`type`} in includeds")))
                    case Some(reos: ResourceObjects) =>
                        Some(ResourceObjects(reos.array.map { reo =>
                            includeds.find(y => y.id == reo.id && y.`type` == reo.`type`)
                                    .getOrElse(throw new Exception(s"not found ${reo.id}&${reo.`type`} in includeds"))
                        }))
                    case None => None
                }
                k -> tmp
            }
//            phLog("expandInfo in fromJsonapi is ===> " + expandInfo)

            /** 将展开的 relationships 赋值到实体中 **/
            val inst_mirror = runtime_mirror.reflect(entity)
            expandInfo.foreach { case (k, v) =>
                val field_symbol = entity_type.member(ru.TermName(k)).asTerm
                val field_mirror = inst_mirror.reflectField(field_symbol)
                val extract_symbol = entity_type.member(ru.TermName("jsonapi_to_" + k)).asMethod
                val extract_mirror = inst_mirror.reflectMethod(extract_symbol)
                field_mirror.set(extract_mirror(v))
            }
            entity
        }

        override def toJsonapi(obj: profile): RootObject = {
            val runtime_mirror = ru.runtimeMirror(obj.getClass.getClassLoader)
            val inst_mirror = runtime_mirror.reflect(obj)
            val inst_symbol = inst_mirror.symbol
            val class_symbol = inst_symbol.typeSignature

            val class_field = inst_symbol.typeSignature.members.filter(p => p.isTerm && !p.isMethod).toList

            /** 判断是否为关联到实体的one属性 **/
            def isConnOneInject(f: ru.Symbol): Boolean =
                f.info <:< ru.typeOf[Option[_]] && f.info.typeArgs.length == 1 &&
                        f.info.typeArgs.head.baseClasses.map(_.name.toString).contains("commonEntity")

            /** 判断是否为关联到实体的many属性 **/
            def isConnManyInject(f: ru.Symbol): Boolean =
                f.info <:< ru.typeOf[Option[List[_]]] && f.info.typeArgs.length == 1 &&
                        f.info.typeArgs.head.typeArgs.head.baseClasses.map(_.name.toString).contains("commonEntity")

            /** 解析关联属性 **/
            val conn_data = class_field.filter(f => isConnOneInject(f) || isConnManyInject(f))
                    .map { field =>
                        val field_name = field.name.toString.trim
                        val field_symbol = class_symbol.member(ru.TermName(field_name)).asTerm
                        val field_mirror = inst_mirror.reflectField(field_symbol)
                        val def_symbol = class_symbol.member(ru.TermName(field_name + "_to_jsonapi")).asMethod
                        val def_mirror = inst_mirror.reflectMethod(def_symbol)
                        field_name -> def_mirror(field_mirror.get).asInstanceOf[Option[RootObject.Data]]
                    }

            /** 阉割关联属性,只保留 id 和 type 存放到 relationships 中 **/
            val relationships: Relationships = conn_data.map { case (k, v) =>
                val tmp = v match {
                    case Some(reo: ResourceObject) =>
                        Relationship(data =
                                Some(ResourceObject(
                                    `type` = reo.`type`,
                                    id = reo.id
                                ))
                        )
                    case Some(reos: ResourceObjects) =>
                        Relationship(data =
                                Some(ResourceObjects(
                                    reos.array.map(reo => ResourceObject(
                                        `type` = reo.`type`,
                                        id = reo.id
                                    ))
                                ))
                        )
                    case None => Relationship()
                }
                k -> tmp
            }.toMap

            /** 将关联属性存到 included 中 **/
            val included: Included = Included(ResourceObjects(
                conn_data.flatMap { case (k, v) =>
                    v match {
                        case Some(reo: ResourceObject) => Seq(reo)
                        case Some(reos: ResourceObjects) => reos.array
                        case None => Seq()
                    }
                }
            ))

            RootObject(
                data = Some(
                    toResourceObject(obj)/** 解析基础数据到attributes **/
                            .copy(relationships = Some(relationships))/** 利用copy函数添加relationships **/
                ),
                included = Some(included)
            )
        }
    }
}
