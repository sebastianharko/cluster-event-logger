package com.harko.cluster.logging

import java.nio.charset.Charset

import akka.actor.ExtendedActorSystem
import akka.event.Logging
import akka.serialization.Serializer
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, _}

class ClusterEventSerializer(actorSystem: ExtendedActorSystem) extends Serializer {

  import org.json4s.jackson.Serialization.write

  private val log = Logging.getLogger(actorSystem, this)

  val UTF8: Charset = Charset.forName("UTF-8")

  implicit val formats = DefaultFormats

  // Completely unique value to identify this implementation of Serializer, used to optimize network traffic.
  // Values from 0 to 16 are reserved for Akka internal usage.
  // Make sure this does not conflict with any other kind of serializer or you will have problems
  override def identifier: Int = 800600015

  override def includeManifest = false

  override def fromBinary(bytes: Array[Byte], manifestOpt: Option[Class[_]]): AnyRef = {
    val str = new String(bytes, UTF8)
    val json: JValue = parse(str)
    log.debug(pretty(render(json)))
    json
  }

  override def toBinary(o: AnyRef): Array[Byte] = {
    val jsonString = write(o)
    val dat = write(o).getBytes(UTF8)
    dat
  }

}
