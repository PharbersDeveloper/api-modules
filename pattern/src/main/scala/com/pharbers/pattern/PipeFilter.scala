package com.pharbers.pattern

import scala.language.postfixOps
import scala.concurrent.duration._
import com.pharbers.pattern.manager.SequenceSteps
import com.pharbers.pattern.steps.{commonResult, commonStep, timeout}

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}


object PipeFilter {
    def prop(originSender: ActorRef, sequence: SequenceSteps): Props = Props(new PipeFilter(originSender, sequence))
}

class PipeFilter(originSender: ActorRef, sequence: SequenceSteps) extends Actor with ActorLogging {

    var tmp: Option[Boolean] = None
    var rst: Option[commonResult] = sequence.cr
    var next: ActorRef = null

    val timeOutSchdule: Cancellable = context.system.scheduler.scheduleOnce(5 second, self, new timeout)

    def receive: PartialFunction[Any, Unit] = {
        case cmd: commonStep =>
            dispatchImpl(cmd)
        case timeout() =>
            originSender ! new timeout
            cancelActor
        case x: AnyRef =>
            println(x)
            ???
    }

    def dispatchImpl(cmd: commonStep): Boolean = {
        println(cmd)
        tmp = Some(true)
        cmd.processes(rst) match {
            case (_, Some(err)) =>
                originSender ! err //error(err)
                cancelActor
            case (Some(r), _) =>
                rst = Some(r)
                println(rst)
                rstReturn()
                cancelActor
            case _ => ???
        }
    }

    def rstReturn(): Unit = tmp match {
        case Some(_) =>
            rst match {
                case Some(r) =>
                    sequence.steps match {
                        case Nil =>
                            originSender ! r
                        case head :: tail =>
                            head match {
                                case c: commonStep =>
                                    next = context.actorOf(PipeFilter.prop(originSender, SequenceSteps(tail, rst)), "pipe")
                                    next ! c
                            }
                        case _ => println("msr error")
                    }
                case _ => Unit
            }
        case _ => println("never go here"); Unit
    }

    def cancelActor: Boolean = {
        timeOutSchdule.cancel
        // context.stop(self) 		// 因为后创建的是前创建的子Actor，当父Actor stop的时候，子Actor 也同时Stop，不能进行传递了
    }
}
