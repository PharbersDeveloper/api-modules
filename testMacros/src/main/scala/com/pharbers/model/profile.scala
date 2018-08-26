package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.One2OneConn

@One2OneConn[company]("company")
case class profile(name: String = "") extends commonEntity
