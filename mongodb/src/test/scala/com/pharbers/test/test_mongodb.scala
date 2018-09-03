
import com.pharbers.jsonapi.json.circe.CirceJsonapiSupport
import com.pharbers.jsonapi.model._
import com.pharbers.macros._
import com.pharbers.util.log.phLogTrait
import com.pharbers.macros.convert.jsonapi.JsonapiMacro._
import com.pharbers.mongodb.dbtrait.dbInstanceManager
import com.pharbers.mongodb.model.request
import com.pharbers.test.model.Person

object test_mongodb extends App with dbInstanceManager with CirceJsonapiSupport with phLogTrait {

	def findOne = {
		val jsonData =
			"""
			  |{
			  |	"data": {
			  |		"id": "1",
			  |		"type": "request",
			  |		"attributes": {
			  |			"res": "test"
			  |		},
			  |		"relationships": {
			  |			"eq_conditions": {
			  |				"data": [{
			  |					"id": "2",
			  |					"type": "eq_cond"
			  |				}, {
			  |					"id": "3",
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
			  |			"value": "18510971868"
			  |		}
			  |	},{
			  |		"id": "3",
			  |		"type": "eq_cond",
			  |		"attributes": {
			  |			"key": "name",
			  |			"value": "Alex"
			  |		}
			  |	}]
			  |}
			""".stripMargin

		val json_data = parseJson(jsonData)
		val jsonapi = decodeJson[RootObject](json_data)

		val requests = formJsonapi[request](jsonapi)
//		phLog(requests.cond2QueryObj())


		val result = queryDBInstance("test").get.queryObject[Person](requests)
		phLog(result)

	}
//	findOne

	def findPerson = {
		val jsonData =
			"""
			  |{
			  |	"data": {
			  |		"id": "1",
			  |		"type": "request",
			  |		"attributes": {
			  |			"res": "Person"
			  |		},
			  |		"relationships": {
			  |			"eq_conditions": {
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
			  |			"key": "name",
			  |			"value": "Alex"
			  |		}
			  |	}]
			  |}
			""".stripMargin

		val json_data = parseJson(jsonData)
		val jsonapi = decodeJson[RootObject](json_data)

		val requests = formJsonapi[request](jsonapi)
//				phLog(requests.cond2QueryObj())

		val result = queryDBInstance("test").get.queryObject[Person](requests)
		phLog(result)
//		result.tips.foreach(phLog(_))
//		result.tag.foreach { x =>
//			phLog(x)
//		}
	}

	findPerson
}