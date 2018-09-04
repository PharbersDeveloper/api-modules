package com.pharbers.mongodb.dbtrait

import com.pharbers.baseModules.PharbersInjectModule
import com.pharbers.moduleConfig.{ConfigDefines, ConfigImpl}
import com.pharbers.mongodb.dbdrive.ConnectionInstance
import com.pharbers.mongodb.dbimpl.MongoDBImpl

import scala.xml.Node

trait dbInstanceManager extends PharbersInjectModule {
	class MongoDBInstance(tmp : ConnectionInstance) extends MongoDBImpl {
		override implicit val dc: ConnectionInstance = tmp
	}

	override val id: String = "mongodb-connect-nodes"
	override val configPath: String = "pharbers_config/db_manager.xml"
	override val md = "connect-config-path" :: Nil

	import com.pharbers.moduleConfig.ModuleConfig.fr
	implicit val f : (ConfigDefines, Node) => ConfigImpl = { (c, n) =>
		ConfigImpl(
			c.md map { x => x -> ((n \ x).toList map { iter =>
				(iter \\ "@name").toString -> new dbInstance((iter \\ "@value").toString)
			})}
		)}
	override lazy val config: ConfigImpl = loadConfig(configDir + "/" + configPath)

	lazy val connections : List[(String, DBTrait)] =
		config.mc.find(p => p._1 == md.head).get._2.
			asInstanceOf[List[(String, ConnectionInstance)]].
			map (iter => iter._1 -> new MongoDBInstance(iter._2))

	def queryDBInstance(name : String) : Option[DBTrait] =
		connections.find(p => p._1 == name).map (x => Some(x._2)).getOrElse(None)

	def queryDBConnection(name : String) : Option[ConnectionInstance] =
		connections.find(p => p._1 == name).
			map (x => Some(x._2.asInstanceOf[MongoDBInstance].dc)).getOrElse(None)
}
