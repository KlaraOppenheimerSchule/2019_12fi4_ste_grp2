package de.kos.sport.routes.student

import de.kos.sport.database.Student
import de.kos.sport.database.VisitedCheckpoint
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class StudentVisitsRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val id = req.params(":id").toIntOrNull()

        val student = transaction { Student.all().find { it.studentId == id } }

        if (student != null) {
            transaction {
                val visits = VisitedCheckpoint.all().filter { it.student.id.value == student.id.value }

                visits.forEachIndexed { id, visit ->
                    sb.append(visit)

                    if (id < visits.count() - 1) {
                        sb.append(", ")
                    }
                }
            }
        } else {
            sb.append("{ \"error\": \"Student not found\" }")
        }

        return sb.append("]").toString()
    }
}