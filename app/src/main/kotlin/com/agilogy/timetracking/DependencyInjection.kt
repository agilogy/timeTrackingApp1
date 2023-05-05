package com.agilogy.timetracking

import arrow.fx.coroutines.ResourceScope
import com.agilogy.db.hikari.HikariCp
import com.agilogy.timetracking.domain.TimeTrackingAppPrd
import com.agilogy.timetracking.postgresdb.PostgresTimeEntriesRepository

suspend fun ResourceScope.timeTrackingApp(): TimeTrackingAppPrd {
    val dataSource = HikariCp.dataSource("jdbc:postgresql://localhost/test", "postgres", "postgres").bind()
    val repo = PostgresTimeEntriesRepository(dataSource)
    return TimeTrackingAppPrd(repo)
}
