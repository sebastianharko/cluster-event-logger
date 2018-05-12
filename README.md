Cluster Event Logging
=====================

This is an Akka extension that logs cluster events to a journal (via Akka Persistence). This extension contributes
a route to [Akka Management] (https://developer.lightbend.com/docs/akka-management/current/). The route simply returns
the event stream.

To install, include this jar in your project and in your ```application.conf``` configure the persistence settings:

```

cluster-event-logging.journal = ${cassandra-journal}

cluster-event-logging.query-journal = "cassandra-query-journal"

```

Use:

```
object Main extends App {

  implicit val system = ActorSystem("system")

  AkkaManagement(system).start()

  // etc.

}
```

Get event stream
================

```
$ curl -XGET http://127.0.0.1:19999/events

data:{"self":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"timestamp":1526144470504,"eventType":"MemberUp","event":{"member":{"uniqueAddress":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"upNumber":1,"status":{},"roles":["dc-default"]}}}

data:{"self":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"timestamp":1526144471232,"eventType":"LeaderChanged","event":{"leader":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}}}

data:{"self":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"timestamp":1526144491390,"eventType":"MemberLeft","event":{"member":{"uniqueAddress":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"upNumber":1,"status":{},"roles":["dc-default"]}}}

data:{"self":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"timestamp":1526144492053,"eventType":"MemberExited","event":{"member":{"uniqueAddress":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"upNumber":1,"status":{},"roles":["dc-default"]}}}

```


Warning
=======

Experimental. Do not use in production.


