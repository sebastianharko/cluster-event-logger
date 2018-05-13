Intro
=====

There's two things here: a cluster event logging extension and a visualizer. The visualizer is a work-in-progress and not yet
functional.

Warning
=======

All this stuff is experimental. Do not use in production.

Cluster Event Logging
=====================

This is an Akka extension that logs cluster events to a journal (via Akka Persistence). This extension contributes
a route to [Akka Management](https://developer.lightbend.com/docs/akka-management/current/). The route simply returns
the event stream.

Requirements
------------

Akka Persistence Cassandra

Installation
------------

To install, include this jar in your project.

Use:

```
object Main extends App {

  implicit val system = ActorSystem("system")

  AkkaManagement(system).start()

  // etc.

}
```

Get event stream
----------------

```
$ curl -XGET http://127.0.0.1:19999/events

data:{"self":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"timestamp":1526144470504,"eventType":"MemberUp","event":{"member":{"uniqueAddress":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"upNumber":1,"status":{},"roles":["dc-default"]}}}

data:{"self":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"timestamp":1526144471232,"eventType":"LeaderChanged","event":{"leader":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}}}

data:{"self":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"timestamp":1526144491390,"eventType":"MemberLeft","event":{"member":{"uniqueAddress":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"upNumber":1,"status":{},"roles":["dc-default"]}}}

data:{"self":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"timestamp":1526144492053,"eventType":"MemberExited","event":{"member":{"uniqueAddress":{"address":{"protocol":"akka.tcp","system":"minimal","host":"127.0.0.1","port":2551}},"upNumber":1,"status":{},"roles":["dc-default"]}}}

```

Visualizer
==========

The visualizer is a small scala.js program that displays the state of the cluster in a visual grid where led (i, j) highlights
the state of node j as seen by node i. This representation is inspired by the Eric Loots' [Pi-Akka-Cluster](https://github.com/lightbend/Pi-Akka-Cluster).
This visualizer is not yet functional.
