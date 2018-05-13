package com.harko.cluster.visualizerjs

object Protocol {

  sealed trait Event

  case class ClusterEvent(observedBy: Address,
    timestamp: Long,
    event: Event
  )

  case class Address(
    protocol: String,
    system: String,
    host: String,
    port: Double
  )

  case class UniqueAddress(
    address: Address
  )
  case class Status(

  )
  case class Member(
    uniqueAddress: UniqueAddress,
    upNumber: Double,
    status: Status,
    roles: List[String]
  )

  case class MemberUp(
    member: Member
  ) extends Event


}

