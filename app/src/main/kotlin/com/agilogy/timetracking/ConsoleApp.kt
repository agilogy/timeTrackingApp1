package com.agilogy.timetracking

import com.agilogy.timetracking.driveradapters.console.ConsoleAdapter

fun main(args: Array<String>): Unit = App {
    ConsoleAdapter(timeTrackingApp()).main(args)
}

