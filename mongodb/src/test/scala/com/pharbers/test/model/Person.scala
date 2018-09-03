package com.pharbers.test.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro


@ToStringMacro
case class Person() extends commonEntity {
	var name: String = ""
	var age: Int = 0
	var phone: String = ""
	var tips: List[String] = Nil
	var tag: List[String Map Any] = Nil
	var tag2: String Map List[Any] = Map.empty
}
