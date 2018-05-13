package com.harko.cluster.visualizerjs

import akka.actor.{Actor, ActorSystem, Props, Stash}
import com.harko.cluster.visualizerjs.Protocol.{Address, ClusterEvent, MemberUp, _}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, document, html}

import scala.scalajs.js.Dynamic.{global => g}
import scala.scalajs.js.annotation.JSExport
import scala.util.{Failure, Try}

case class ServerSentMessage(data: String)

case object Draw

case class DrawAt(i: Int)

case class GridSnapshot(addresses: List[Address], spots: Map[(Int, Int), String])

class Grid() extends Actor with Stash {

  var ctx: CanvasRenderingContext2D = null

  // current state
  var addresses: List[Address] = List[Address]()
  var spots: Map[(Int, Int), String] = Map[(Int, Int), String]()

  var history: List[(Long, GridSnapshot)] = List()

  def assignSpot(address: Protocol.Address): Int = {
    val newAddrs: List[Address] = addresses ++ (if (addresses.contains(address)) Nil else List(address))
    addresses = newAddrs
    addresses.indexOf(address) + 1
  }

  override def receive: Receive = {
    case ctx: CanvasRenderingContext2D =>
      this.ctx = ctx
      import scala.scalajs.js
      import js.Dynamic.{global => g}
      g.console.log("actor initialized")
      unstashAll()
      context.become(initialized)
    case msg =>
      g.console.log("stashing message")
      stash()
  }

  def initialized: Receive = {
    case clusterEvent @ ClusterEvent(observer, timestamp, MemberUp(Member(UniqueAddress(observee: Address), _, _, _))) =>
      val i: Int = assignSpot(observer)
      val j: Int = assignSpot(observee)
      val item = (i, j) -> "limegreen"
      spots = spots + item
      history = history :+ (timestamp -> GridSnapshot(addresses, spots))


    case DrawAt(i: Int) =>
      Try(history(i)) match {
        case Failure(_) =>
          g.console.log("no data for that point in time:" + i)
        case scala.util.Success((timestamp, state)) =>

          val max = state.addresses.size
          ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height)
          (1 to max).foreach { i: Int =>
            (1 to max).foreach { j: Int => {
              ctx.beginPath()
              ctx.arc(40 + j * 40, 40 + i * 40, 15, 0, 2 * Math.PI)
              val fillColour = state.spots.getOrElse(i -> j, "white")
              ctx.fillStyle = fillColour
              ctx.fill()
              ctx.stroke()
              ctx.closePath()
            }
            }
          }
      }

  }

}

@JSExport
object TutorialApp {

  val a = Address("", "", "", 2551)
  val b = Address("", "", "", 2552)
  val c = Address("", "", "", 2553)


  lazy val config: Config =
    ConfigFactory
      .parseString("""
      stash-custom-mailbox {
        mailbox-type = "akka.dispatch.UnboundedDequeBasedMailbox"
      }
      """
      ).withFallback(akkajs.Config.default)

  val system = ActorSystem("clustergrid", config)
  val mainActor = system.actorOf(Props(new Grid()).withMailbox("stash-custom-mailbox"))

  @JSExport
  def rangeChanged(value: String): Unit = {
    g.console.log("range changed:" + value)
    mainActor ! DrawAt(value.toInt)
  }

  @JSExport
  def clicked(): Unit = {
    g.console.log("button clicked")
    val inp = document.getElementById("myRange").asInstanceOf[html.Input]
    inp.max = (inp.max.toInt + 1).toString
  }

  @JSExport
  def main(canvas: html.Canvas): Unit = {

    val ctx: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    mainActor ! ctx

  }

}
