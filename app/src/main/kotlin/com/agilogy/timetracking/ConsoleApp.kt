package com.agilogy.timetracking

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.resourceScope
import com.agilogy.timetracking.driveradapters.console.ConsoleAdapter

fun main(args: Array<String>): Unit = SuspendApp {
    resourceScope {
        ConsoleAdapter(timeTrackingApp()).main(args)
    }
}

