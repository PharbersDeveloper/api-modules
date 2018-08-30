package com.pharbers.test

import com.pharbers.model._
import com.pharbers.util.log.phLogTrait
import com.pharbers.macros.JsonapiConvert

class TestJsonapiConvert() extends JsonapiConvert[profile] with phLogTrait {

    import com.pharbers.jsonapi.model._
    import com.pharbers.macros.convert.jsonapi._
    import com.pharbers.jsonapi.model.RootObject._
    import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._

    override def fromJsonapi(jsonapi: RootObject): profile = {

        val jsonapi_data = jsonapi.data.get.asInstanceOf[ResourceObject]
        val included = jsonapi.included
        val entity = fromResourceObject[profile](jsonapi_data, included)(ResourceReaderMaterialize)
//        phLog("entity in fromJsonapi is ===> " + entity)

        entity
    }

    override def toJsonapi(obj: profile): RootObject = {
        val reo_includeds = toResourceObject(obj)

        RootObject(
            data = Some(reo_includeds._1),
            included = Some(reo_includeds._2)
        )
    }

    override def toJsonapi(objs: List[profile]): RootObject = {
        val data_included_lst = objs.map(toJsonapi)
        val dataLst = data_included_lst.map(_.data).filter(_.isDefined).map(_.get.asInstanceOf[ResourceObject])
        val includedLst = data_included_lst.map(_.included).filter(_.isDefined).flatMap(x => x.get.resourceObjects.array).distinct
        RootObject(
            data = if(dataLst.isEmpty) None else Some(ResourceObjects(dataLst)),
            included = if(includedLst.isEmpty) None else Some(Included(ResourceObjects(includedLst)))
        )
    }
}
