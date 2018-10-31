package org.aktor.core

import org.junit.jupiter.api.Test

internal class PingPongTest {

    val supervisor = ActorSystem.createSupervisor()

    @Test
    fun pingPong() {

        val pongActor = supervisor.createStatelessActor<String>("ponger") {
            println("received ${it.payload} from ${it.sender.name} in Thread ${Thread.currentThread().id}")
            "pong".sendTo(it.sender)
        }

        val pingActor = supervisor.createStatelessActor<String>("pinger") {
            println("received ${it.payload} from ${it.sender.name} in Thread ${Thread.currentThread().id}")
            "ping".sendTo(it.sender)
        }

        supervisor.runForAWhile(10000) {
            pongActor.receive(Envelope(pingActor, "start!"))
        }
    }

}