package com.agilogy.timetracking.httpapi

import com.agilogy.json.json
import com.agilogy.json.jsonArray
import com.agilogy.json.jsonObject
import com.agilogy.timetracking.domain.DeveloperName
import com.agilogy.timetracking.domain.ProjectName
import com.agilogy.timetracking.domain.TimeEntry
import com.agilogy.timetracking.domain.TimeTrackingAppPrd
import com.agilogy.timetracking.domain.localDate
import com.agilogy.timetracking.domain.test.InMemoryTimeEntriesRepository
import com.agilogy.timetracking.driveradapters.httpapi.TimeTrackingApi
import io.kotest.core.spec.style.FunSpec
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class TimeTrackingApiTest : FunSpec() {

    private fun withApp(vararg timeEntries: TimeEntry, f: suspend (HttpClient) -> Unit) = testApplication {
        val timeEntriesRepository = InMemoryTimeEntriesRepository()
        timeEntriesRepository.saveTimeEntries(timeEntries.toList())
        val timeEntriesController = TimeTrackingApi(TimeTrackingAppPrd(timeEntriesRepository))
        application { timeEntriesController.routes() }
        val client: HttpClient = createClient {}
        f(client)
    }

    init {

        val john = DeveloperName("john")
        val agilogySchool = ProjectName("Agilogy school")
        val now = Instant.now()
        val today = LocalDate.now()
        val startOfMonth = LocalDate.of(today.year, today.month, 1)
        val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)

        test("return OK with an empty list of entries") {
            withApp { client ->
                val response =
                    client.get("/time-entries/daily-user-hours?userName=${john.name}&startDate=$startOfMonth&endDate=$endOfMonth")
                assertEquals(HttpStatusCode.OK, response.status, response.bodyAsText())
                assertEquals(jsonArray().toString(), response.bodyAsText())
            }
        }

        test("return OK with a not empty list of entries") {
            val startTime = now.minus(2, ChronoUnit.HOURS)
            val startDate = startTime.localDate()
            withApp(TimeEntry(john, agilogySchool, startTime..now)) { client ->
                val response =
                    client.get("/time-entries/daily-user-hours?userName=${john.name}&startDate=$startOfMonth&endDate=$endOfMonth")
                assertEquals(HttpStatusCode.OK, response.status, response.bodyAsText())
                assertEquals(
                    jsonArray(jsonObject("date" to startDate.toString().json, "project" to "Agilogy school".json, "hours" to 2.json))
                        .toString(),
                    response.bodyAsText()
                )
            }
        }
    }
}
