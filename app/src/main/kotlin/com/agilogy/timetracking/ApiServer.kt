package com.agilogy.timetracking

import arrow.continuations.SuspendApp
import arrow.continuations.ktor.server
import arrow.fx.coroutines.resourceScope
import com.agilogy.timetracking.driveradapters.httpapi.TimeTrackingApi
import io.ktor.server.netty.Netty
import kotlinx.coroutines.awaitCancellation
import kotlin.time.Duration.Companion.seconds

fun main() = SuspendApp {
    resourceScope {
        val timeTrackingApi = TimeTrackingApi(timeTrackingApp())
        server(Netty, port = 8080, host = "0.0.0.0", preWait = 5.seconds) {
            timeTrackingApi.routes()
        }
        awaitCancellation()
    }
}