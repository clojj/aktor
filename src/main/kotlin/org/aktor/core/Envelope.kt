package org.aktor.core

data class Envelope<T> (val sender:Actor<T>, val payload: T)
