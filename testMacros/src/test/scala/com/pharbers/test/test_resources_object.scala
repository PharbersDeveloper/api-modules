package com.pharbers.test

import io.circe.syntax._
import com.pharbers.jsonapi.model._
import com.pharbers.util.log.phLogTrait
import com.pharbers.macros.convert.jsonapi._
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.jsonapi.model.RootObject.ResourceObject

object test_resources_object extends App with CirceJsonapiSupport with phLogTrait {
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
 |   },
 |   "relationships" : {
 |      "profile" : {}
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

    val entity = fromResourceObject(resources)(new TestResourceConvert())
//    import com.pharbers.macros.convert.jsonapi.ResourceObjectReader._
//    val entity = fromResourceObject[profile](resources)
    println(entity)

    val result = toResourceObject(entity)(new TestResourceConvert())
//    val result = toResourceObject(entity)
    println(result)
    println(result.asJson)
}