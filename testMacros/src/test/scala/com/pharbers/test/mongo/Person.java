package com.pharbers.test.mongo;

@ToStringMacro
case class Person() extends commonEntity {
	var name: String = ""
	var age: Int = 0
	var phone: String = ""
//	var tips: List[_] = Nil
//	var tag: List[_] = Nil
//	var tag2: Map[_, _] = Map.empty
}
