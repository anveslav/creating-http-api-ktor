package com.jetbrains.handson.httpapi.routes

import com.jetbrains.handson.httpapi.model.Customer
import com.jetbrains.handson.httpapi.model.customerStorage
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*

fun Route.customerRouting() {
    route("/customer") {
        get() {
            if (customerStorage.isNotEmpty()) {
                call.respond(customerStorage)
            } else {
                call.respondText("No customers found", status = HttpStatusCode.NotFound)
            }
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )

            val customer = customerStorage.find { it.id == id } ?: return@get call.respondText(
                "No customer found with is $id", status = HttpStatusCode.NotFound
            )
            call.respond(customer)

        }
        post {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Accepted)

        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (customerStorage.removeIf { it.id == id }) {
                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Customer nor found", status = HttpStatusCode.NotFound)
            }
        }
    }
}

fun Application.registerCustomerRoutes() {
    routing {
        customerRouting()
    }
}