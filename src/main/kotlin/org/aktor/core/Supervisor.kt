package org.aktor.core

import kotlinx.coroutines.*

class Supervisor : ActorContext {

    fun <M> createStatelessActor(name: String, behavior: Actor<M>.(Envelope<M>) -> Unit): Actor<M> =
            StatelessActor(this, name, behavior).also { actors.add(it) }

    private val job = Job()

    val actors = mutableListOf<Actor<*>>()

    fun stop() {
        actors.forEach { it.stop() } //first gives a chance for actors to close gracefully

        job.cancel()
        // Cancel job on activity destroy. After destroy all children jobs will be cancelled automatically
        actors.removeAll { true }
    }


    fun runForAWhile(timeoutInMillis: Long, init: () -> Unit) {

        runBlocking(newFixedThreadPoolContext(1, "SupervisorPool")) {
            stopAfterAWhile(timeoutInMillis)
            start()
            init()
        }
    }

    fun start() {

        actors.forEach { it.start() }
    }

    private fun stopAfterAWhile(timeoutInMillis: Long) {

        scope().launch {
            Thread.sleep(timeoutInMillis)
            stop()
        }
    }

    override fun scope() = CoroutineScope(job)
}