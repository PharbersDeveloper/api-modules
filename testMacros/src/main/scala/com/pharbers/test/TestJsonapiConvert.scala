package com.pharbers.test

import com.pharbers.model._
import com.pharbers.macros.JsonapiConvert
import com.pharbers.util.log.phLogTrait

class TestJsonapiConvert() extends JsonapiConvert[people] with phLogTrait {

    import com.pharbers.jsonapi.model._
    import com.pharbers.macros.convert.jsonapi._
    import scala.reflect.runtime.{universe => ru}
    import com.pharbers.jsonapi.model.RootObject._
    import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._

    override def fromJsonapi(jsonapi: RootObject): people = {
        val entity_type = ru.typeOf[people]
        val runtime_mirror = ru.runtimeMirror(getClass.getClassLoader)

        /** 解析attributes 到基础数据 **/
        val jsonapi_data = jsonapi.data.get.asInstanceOf[ResourceObject]
        val entity = fromResourceObject[people](jsonapi_data)(ResourceReaderMaterialize)
//            phLog("entity in fromJsonapi is ===> " + entity)

        /** 根据relationships 找到关联的includeds **/
        val relationships = jsonapi_data.relationships
        val expandInfo = relationships match {
            case Some(relp) =>
                val includeds = jsonapi.included.getOrElse(throw new Exception("not found included in RootObject")).resourceObjects.array
                relp.map { case (k, v) =>
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
            case None => Map().empty
        }
//            phLog("expandInfo in fromJsonapi is ===> " + expandInfo)

        /** 将展开的 relationships 赋值到实体中 **/
        val inst_mirror = runtime_mirror.reflect(entity)
        expandInfo.foreach { case (k, v) =>
            val field_symbol = try {
                entity_type.member(ru.TermName(k)).asTerm
            } catch {
                case _: scala.ScalaReflectionException =>
                    throw new Exception(s"not found member $k in people")
            }
            val field_mirror = inst_mirror.reflectField(field_symbol)
            val extract_symbol = entity_type.member(ru.TermName("jsonapi_to_" + k)).asMethod
            val extract_mirror = inst_mirror.reflectMethod(extract_symbol)
            try {
                field_mirror.set(extract_mirror(v))
            } catch {
                case _: java.lang.reflect.InvocationTargetException =>
                    throw new Exception(s"unable to parse to people connected $k , param = $v")
            }
        }
        entity
    }

    override def toJsonapi(obj: people): RootObject = {
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
                }.filter(_._2.isDefined)

        /** 阉割关联属性,只保留 id 和 type 存放到 relationships 中 **/
        val relationships: Option[Relationships] = if (conn_data.isEmpty) None
        else { Some(
            conn_data.map { case (k, v) =>
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
                                    )).distinct
                                ))
                        )
                    case None => Relationship()
                }
                k -> tmp
            }.toMap
        )}


        /** 将关联属性存到 included 中 **/
        val included: Option[Included] = if(conn_data.isEmpty) None
        else {
            Some(Included(ResourceObjects(
                conn_data.flatMap { case (_, v) =>
                    v match {
                        case Some(reo: ResourceObject) => Seq(reo)
                        case Some(reos: ResourceObjects) => reos.array
                        case None => Seq()
                    }
                }.foldLeft(List.empty[ResourceObject]){
                    (result, cur) => if(result.exists(x => x.id == cur.id && x.`type` == cur.`type`)) result else result :+ cur
                }
            )))
        }

        RootObject(
            data = Some(
                toResourceObject(obj)/** 解析基础数据到attributes **/
                        .copy(relationships = relationships)/** 利用copy函数添加relationships **/
            ),
            included = included
        )
    }

    override def toJsonapi(objs: List[people]): RootObject = {
        val dataLst = objs.map(toJsonapi).map(_.data).filter(_.isDefined).map(_.get.asInstanceOf[ResourceObject])
        val includedLst = objs.map(toJsonapi).map(_.included).filter(_.isDefined).flatMap(x => x.get.resourceObjects.array).distinct
        RootObject(
            data = if(dataLst.isEmpty) None else Some(ResourceObjects(dataLst)),
            included = if(includedLst.isEmpty) None else Some(Included(ResourceObjects(includedLst)))
        )
    }
}
