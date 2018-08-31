package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting._

@ToStringMacro
case class guardian() extends commonEntity {
   var address: String =  "guardian_address1"
   var mobile = "guardian_mobile1"
   var name = "guardian_郭监护人1"
   var nickname = "guardian_nickname1"
   var wechatid = "guardian_wechatid1"
}
