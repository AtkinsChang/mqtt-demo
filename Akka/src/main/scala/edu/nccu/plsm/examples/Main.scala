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
package edu.nccu.plsm.examples

import edu.nccu.plsm.util.Colors
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

object Main extends App with AkkaMqttExample with AppConfig {
  private[this] val logger = LoggerFactory.getLogger(getClass)
  logger info "Application starting..."
  if (logger.isInfoEnabled) {
    BuildInfo.toMap.foreach{
      case (k, v) => logger info s" $k: $v"
    }
  }
  override val message = StdIn.readLine("Please input the message in String:")

  val system = runAkkaMqttExample
  sys addShutdownHook {
    Thread.currentThread.setName(s"ActorSystem (${system.name}) - ShutdownTask")
    logger info Colors.redBackground("Terminating actor system...")
    Await ready(system.terminate(), Duration.Inf)
    logger info Colors.redBackground("Shutdown hook finished.")
  }
}
