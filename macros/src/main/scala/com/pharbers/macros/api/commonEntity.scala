package com.pharbers.macros.api

import com.pharbers.pattern.entity.commonResult

trait commonEntity extends commonResult {
    var id: String = ""
    var `type`: String = ""
    var major: Int = 1
    var minor: Int = 0
}
