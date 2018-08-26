package com.pharbers.test

import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.jsonapi.model.RootObject
import com.pharbers.model.{company, profile}
import com.pharbers.util.log.phLogTrait

object debugMacro extends App with CirceJsonapiSupport with phLogTrait {
    val test_data = """
          {
 | "data": {
 |  "id": "01",
 |  "type": "Contact",
 |  "attributes": {
 |   "name": "jeorch",
 |   "phone": "17611134431"
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

//    val entity = formJsonapi[user](jsonapi)


    val c0 = company("company - 0")
    val c1 = company("company - 1")
    val u0 = profile("user - 0")
    val u1 = profile("user - 1")
    val u2 = profile("user - 2")

    println(u0)
    u0.company = Some(c0)
    println(u0)
    u0.name = "hhahaha"
    u0.company = Some(c1)
    println(u0)

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
