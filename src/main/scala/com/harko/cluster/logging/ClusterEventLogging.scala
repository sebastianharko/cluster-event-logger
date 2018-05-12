package com.harko.cluster.logging

import akka.actor.{ActorLogging, ExtendedActorSystem, Extension, ExtensionId, Props}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, UniqueAddress}
import akka.event.Logging
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.management.http.{ManagementRouteProvider, ManagementRouteProviderSettings}
import akka.persistence.{PersistentActor, Recovery}
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.scaladsl.EventsByPersistenceIdQuery
import akka.stream.ActorMaterializer
import org.json4s.JsonAST.JValue
import org.json4s.jackson.JsonMethods._

import scala.concurrent.duration._

case class ClusterEvent[T <: ClusterDomainEvent](self: UniqueAddress,
  timestamp: Long,
  eventType: String,
  event: T)

class ClusterEventLogger extends PersistentActor with ActorLogging {

  val cluster = Cluster(context.system)

  override def journalPluginId: String = "cluster-event-logging.journal"

  override def recovery: Recovery = Recovery.none

  cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
    classOf[MemberUp],
    classOf[MemberLeft],
    classOf[MemberExited],
    classOf[MemberJoined],
    classOf[MemberRemoved],
    classOf[MemberWeaklyUp],
    classOf[ReachableMember],
    classOf[UnreachableMember],
    classOf[LeaderChanged])


  override def receiveRecover: Receive = {
    case _ => // ignore
  }

  override def receiveCommand: Receive = {
    case e: ClusterDomainEvent =>
      persist(ClusterEvent(cluster.selfUniqueAddress,
        timestamp = System.currentTimeMillis(),
        eventType = e.getClass.getName.split("\\$").last,
        event = e)) {
        _ => // do nothing
      }
  }

  override def persistenceId: String = cluster.selfAddress.toString

}

class ClusterEventLogging(system: ExtendedActorSystem) extends Extension with ManagementRouteProvider {

  val log = Logging(system, classOf[ClusterEventLogging])
  log.info("starting ClusterEventLogging extension")

  implicit val materializer: ActorMaterializer = ActorMaterializer()(system)

  val cluster: Cluster = Cluster(system)

  // start the ClusterEventLogger actor
  system.actorOf(Props(new ClusterEventLogger), "cluster-event-logger")

  val queries = PersistenceQuery(system)
    .readJournalFor[EventsByPersistenceIdQuery](system.settings.config.getString("cluster-event-logging.query-journal"))

  import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
  override def routes(settings: ManagementRouteProviderSettings): Route =
  path("events") {
    get {
      complete {
        queries.eventsByPersistenceId(cluster.selfAddress.toString, 0, Long.MaxValue)
          .map(eventEnvelope => ServerSentEvent(compact(render(eventEnvelope.event.asInstanceOf[JValue]))))
          .keepAlive(1.second, () => ServerSentEvent.heartbeat)
      }
    }
  }

}

object ClusterEventLogging extends ExtensionId[Extension] {
  override def createExtension(system: ExtendedActorSystem): Extension = new ClusterEventLogging(system)
}