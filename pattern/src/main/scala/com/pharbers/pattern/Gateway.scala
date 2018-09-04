package com.pharbers.pattern

import play.api.libs.json.Json.toJson
import com.pharbers.pattern.manager.SequenceSteps
import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}
import com.pharbers.pattern.steps._

class Gateway extends Actor with ActorLogging {

    var originSender: ActorRef = null
    var next: ActorRef = null

    def receive: PartialFunction[Any, Unit] = {
        case excute(sequence) =>
            originSender = sender
            sequence.steps match {
                case Nil => originSender ! new commonError(0, "error")
                case head :: tail =>
                    head match {
                        case _: commonStep =>
                            next = context.actorOf(PipeFilter.prop(self, SequenceSteps(tail, sequence.cr)), "gate")
                            next ! head
                    }

                    context.watch(next)
            }
        case rst: commonResult =>
            originSender ! rst
            cancelActor()
        case err: commonError =>
            originSender ! err
            cancelActor()
        case timeout() =>
            originSender ! toJson("timeout")
            cancelActor()
        case Terminated(actorRef) => println("Actor {} terminated", actorRef)
        case _ => Unit
    }

    def cancelActor(): Unit = {
        context.stop(self)
    }
}
