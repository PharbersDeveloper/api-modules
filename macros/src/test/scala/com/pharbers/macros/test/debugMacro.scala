package com.pharbers.macros.test

import com.pharbers.jsonapi.model._
import com.pharbers.util.log.phLogTrait
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport

object debugMacro extends App with CirceJsonapiSupport with phLogTrait {
    val test_data =
        """
          |{
          |	"data": {
          |		"id": "1",
          |		"type": "request",
          |		"attributes": {
          |			"res": "BMPhone"
          |		},
          |		"relationships": {
          |			"conditions": {
          |				"data": [{
          |					"id": "2",
          |					"type": "eq_cond"
          |				}]
          |			}
          |		}
          |	},
          |	"included": [{
          |		"id": "2",
          |		"type": "eq_cond",
          |		"attributes": {
          |			"key": "phone",
          |			"val": "13720200891"
          |		}
          |	}]
          |}
            """.stripMargin
    val json_data = parseJson(test_data)
    val jsonapi = decodeJson[RootObject](json_data)
    phLog(jsonapi)

//    @ConnectionsMacro
//    @ConnOne2One("user", "user")
//    @ConnOne2Many("company", "company")
    class userdetailresult(id : String,
                           major : java.lang.Integer,
                           minor : java.lang.Integer
                          )
}
