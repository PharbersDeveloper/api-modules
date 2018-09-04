package com.pharbers.pattern.steps

trait commonStep {
    val module : String
    val methed : String
    val args : commonResult

    def processes(pr : Option[commonResult]) : (Option[commonResult], Option[commonError])
}
