package com.agilogy.timetracking

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.resourceScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.logging.LogManager
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

fun App(
    context: CoroutineContext = Dispatchers.Default,
    timeout: Duration = Duration.INFINITE,
    block: suspend context(CoroutineScope, ResourceScope) () -> Unit,
) = SuspendApp(context, timeout) {
    this.javaClass.getResourceAsStream("/logging.properties").use {
        LogManager.getLogManager().readConfiguration(it)
    }
    resourceScope {
        block(this@SuspendApp, this)
    }
}