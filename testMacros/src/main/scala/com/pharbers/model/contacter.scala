package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.ToStringMacro

@ToStringMacro
case class contacter() extends commonEntity {
    var address: String =  "contacter_address1"
    var mobile = "contacter_mobile1"
    var name = "contacter_郭监护人1"
    var nickname = "contacter_nickname1"
    var wechatid = "contacter_wechatid1"
}
