package com.pharbers.pattern.entry

import play.api.mvc._
import akka.pattern.ask
import akka.util.Timeout
import javax.inject.Inject
import com.pharbers.pattern.Gateway

import scala.concurrent.Await
import scala.language.postfixOps
import play.api.libs.json.JsValue

import scala.concurrent.duration._
import com.pharbers.pattern.entity.commonResult
import com.pharbers.pattern.manager.SequenceSteps
import akka.actor.{ActorSystem, Props}
import com.pharbers.pattern.steps.excute
import play.api.libs.Files.TemporaryFile


object PlayEntry {
    def apply()(implicit akkasys : ActorSystem, cc : ControllerComponents) = new PlayEntry()
}

class PlayEntry @Inject() (implicit akkasys : ActorSystem, cc : ControllerComponents) extends AbstractController(cc) {
    implicit val t: Timeout = Timeout(5 second)

    def commonExcution(msr : SequenceSteps) : commonResult = {
        val act = akkasys.actorOf(Props[Gateway])
        val r = act ? excute(msr)
        Await.result(r.mapTo[commonResult], t.duration)
    }

    def uploadRequestArgs(request : Request[AnyContent])(func : MultipartFormData[TemporaryFile] => JsValue) : Result = {
        try {
            request.body.asMultipartFormData.map { x =>
                Ok(func(x))
            }.getOrElse (BadRequest("Bad Request for input"))
        } catch {
            case _ : Exception => BadRequest("Bad Request for input")
        }
    }
}
