package com.agilogy.timetracking.driveradapters.httpapi

import arrow.core.Either
import arrow.core.getOrElse
import com.agilogy.json.json
import com.agilogy.json.jsonObject
import com.agilogy.timetracking.domain.DeveloperName
import com.agilogy.timetracking.domain.TimeTrackingApp
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.JsonPrimitive
import java.time.LocalDate

class TimeTrackingApi(private val timeEntriesRegister: TimeTrackingApp) {

    context(Application)
    fun routes() = routing { timeEntriesRoutes() }

    context(Routing)
    fun timeEntriesRoutes() {
        // GET /time-entries/daily-user-hours?userName=userName&startDate=startDate&endDate=endDate
        get("/time-entries/daily-user-hours") {

            Either.zipOrAccumulate(
                call.request.queryParameters.parseRequiredParam("userName") { DeveloperName(it) },
                call.request.queryParameters.parseRequiredParam("startDate") { LocalDate.parse(it) },
                call.request.queryParameters.parseRequiredParam("endDate") { LocalDate.parse(it) }
            ) { userName, startDate, endDate ->
                val result = timeEntriesRegister.getDeveloperHoursByProjectAndDate(userName, startDate..endDate)
                val jsonResult = result.map { (date, project, hours) ->
                    jsonObject("date" to date.toString().json, "project" to project.name.json, "hours" to hours.value.json)
                }.json
                call.respondText(jsonResult.toString(), ContentType.Application.Json)
            }.getOrElse { errors ->
                call.respondText(
                    jsonObject("validationErrors" to jsonObject(errors.map { it.field to JsonPrimitive(it.description) })).toString(),
                    ContentType.Application.Json,
                    HttpStatusCode.BadRequest
                )
            }

        }
    }

    private fun <A> Parameters.parseRequiredParam(param: String, parser: (String) -> A): Either<ValidationError, A> =
        this[param]?.let {
            Either.catch { parser(it) }.mapLeft { ValidationError(param, "invalid.format") }
        } ?: Either.Left(ValidationError(param, "required"))
}

data class ValidationError(val field: String, val description: String)


