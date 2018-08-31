package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._

@One2ManyConn[contacter]("contacts")
@One2ManyConn[guardian]("guardians")
@ToStringMacro
case class people() extends commonEntity {
    var age: Int =  1
    var birthday = "20180808"
    var found = 1535001930
    var name = "老郭"
    var nickname = "老郭nick"
    var photo = "照片1"
    var school = "北大"
    var sex = "男"
}
