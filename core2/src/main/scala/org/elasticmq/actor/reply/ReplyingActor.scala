package org.elasticmq.actor.reply

import akka.actor.Actor
import akka.actor.Status.Failure
import scala.reflect.ClassTag
import scala.language.higherKinds

trait ReplyingActor extends Actor {
  type M[X] <: Replyable[X]
  val ev: ClassTag[M[Unit]]

  def receive = {
    case m if ev.runtimeClass.isAssignableFrom(m.getClass) => {
      try {
        sender ! receiveAndReply(m.asInstanceOf[M[Unit]])
      } catch {
        case e: Exception => sender ! Failure(e)
      }
    }
  }

  def receiveAndReply[T](msg: M[T]): T
}