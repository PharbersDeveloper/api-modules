package com.pharbers.model.detail

import com.pharbers.macros.common.connecting.{ConnOne2Many, ConnOne2One, ConnectionsMacro}
import com.pharbers.macros.common.expending.Expandable

//@JsonCodec
@ConnectionsMacro
@ConnOne2One("user", "user")
@ConnOne2Many("company", "company")
class userdetailresult(id : String,
                       major : java.lang.Integer,
                       minor : java.lang.Integer
                      )

