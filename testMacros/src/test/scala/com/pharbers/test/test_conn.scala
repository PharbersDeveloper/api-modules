package com.pharbers.test

import com.pharbers.model.{company, profile}

object test_conn extends App {

    val c0 = company("company - 0")
    val c1 = company("company - 1")
    val u0 = profile("user - 0")
    val u1 = profile("user - 1")
    val u2 = profile("user - 2")

    println(u0.id + u0.`type` + u0.major + u0.minor)
    println(u0)
    u0.company = Some(c0)
    println(u0)
    u0.name = "hhahaha"
    u0.company = Some(c1)
    println(u0)

}
