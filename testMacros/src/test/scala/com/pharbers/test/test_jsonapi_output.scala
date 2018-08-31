//package com.pharbers.test
//
//import io.circe.syntax._
//import com.pharbers.model._
//import com.pharbers.macros._
//import com.pharbers.util.log.phLogTrait
//import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
//
//object test_jsonapi_output extends App with CirceJsonapiSupport with phLogTrait {
//    val entity = people()
//    entity.id = "5b7e454a8fb8076c3c3304c7"
//
//    val contacter1 = contacter()
//    contacter1.id = "contacter1"
//    val contacter2 = contacter()
//    contacter2.id = "contacter2"
//    contacter2.address =  "contacter_address2"
//    contacter2.mobile =  "contacter_mobile2"
//    contacter2.name = "contacter_郭监护人2"
//    contacter2.nickname = "contacter_nickname2"
//    contacter2.wechatid = "contacter_wechatid2"
//    entity.contacts = Some(contacter1 :: contacter2 :: Nil)
//
//    val guardian1 = guardian()
//    guardian1.id = "guardian1"
//    val guardian2 = guardian()
//    guardian2.id = "guardian1"
//    guardian2.address =  "guardian_address2"
//    guardian2.mobile =  "guardian_mobile2"
//    guardian2.name = "guardian_郭监护人2"
//    guardian2.nickname = "guardian_nickname2"
//    guardian2.wechatid = "guardian_wechatid2"
//    entity.guardians = Some(guardian1 :: guardian2 :: Nil)
//
//    phLog(entity)
//
//    val result = toJsonapi(entity)(new TestJsonapiConvert())
//    phLog(result.asJson)
//}
