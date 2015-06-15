/*
 * Licensed to the Programming Language and Software Methodology Lab (PLSM)
 * under one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership.
 * The PLSM licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.nccu.plsm.examples.actor

import java.util.concurrent.atomic.AtomicLong

import akka.actor._
import edu.nccu.plsm.examples.actor.MessageCreator.CreateMessage

import scala.concurrent.duration._
import scala.language.postfixOps

class MessageCreator(msg: Any, duration: FiniteDuration, target: ActorRef) extends Actor with DiagnosticActorLogging {
  private[this] val counter = new AtomicLong
  private[this] var tick: Cancellable = _

  override def preStart(): Unit = {
    import context.dispatcher
    super.preStart()
    log info "Message creator started."
    tick = context.system.scheduler.schedule(Duration.Zero, duration, self, CreateMessage)
    log info("Message scheduled at rate {}.", duration.toString())
  }

  override def postStop(): Unit = {
    super.postStop()
    log info "Stopping scheduled message."
    tick cancel()
    log info "Message creator stopped."
  }

  override def receive: Receive = {
    case CreateMessage =>
      target ! msg
      log info("Message send. [count={}]", counter.incrementAndGet())
    case any =>
      log error("Received unknown message [{}]", any)
  }
}

object MessageCreator {

  case object CreateMessage

}
