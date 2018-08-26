package com.pharbers.model

import com.pharbers.macros.api.commonEntity
import com.pharbers.macros.common.connecting.One2ManyConn

@One2ManyConn[profile]("users")
case class company(company_name: String = "") extends commonEntity
