//package com.pharbers.test
//
//import io.circe.syntax._
//import com.pharbers.macros._
//import com.pharbers.jsonapi.model._
//import com.pharbers.util.log.phLogTrait
//import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
//
//object test_jsonapi_input extends App with CirceJsonapiSupport with phLogTrait {
//
//    val test_data1 =
//        s"""
//           |{
//           | "data": {
//           |  "id": "1",
//           |  "type": "request",
//           |  "attributes": {
//           |   "res": "BMPhone"
//           |  },
//           |  "relationships": {
//           |   "conditions": {
//           |    "data": [{
//           |     "id": "2",
//           |     "type": "eq_cond"
//           |    }]
//           |   }
//           |  }
//           | },
//           | "included": [{
//           |  "id": "2",
//           |  "type": "eq_cond",
//           |  "attributes": {
//           |   "key": "phone",
//           |   "val": "13720200891"
//           |  }
//           | }]
//           |}""".stripMargin
//    val test_data =
//        s"""
//           |{
//           | "data": {
//           |  "id": "1",
//           |  "type": "request",
//           |  "attributes": {
//           |    "res": "BMPhone"
//           |  }
//           | }
//           |}""".stripMargin
//    val json_data = parseJson(test_data)
//    val jsonapi = decodeJson[RootObject](json_data)
//    phLog(jsonapi)
//
//    val entity = formJsonapi(jsonapi)(new TestJsonapiConvert())
////    import com.pharbers.macros.convert.jsonapi.JsonapiMacro._
////    val entity = formJsonapi[request](jsonapi)
//    phLog(entity)
//
//    val result = toJsonapi(entity)(new TestJsonapiConvert())
////    val result = toJsonapi(entity)
//    phLog(result)
//    phLog(result.asJson)
//}
