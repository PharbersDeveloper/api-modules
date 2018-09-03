package com.pharbers.mongodb.dbtrait

import com.pharbers.mongodb.dbdrive.ConnectionInstance

class dbInstance(cp : String) extends ConnectionInstance {
	override val configPath: String = cp
}
