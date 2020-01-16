package de.kos.sport.routes.student

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class StudentVisitsRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val id = req.params(":id").toIntOrNull()

        if (id == null) {
            sb.append("{ \"error\": \"Id needs to be an integer\" }")
        } else {
            val student = DBConnector.getStudentByStudentId(id)

            if (student != null) {
                transaction {
                    val visits = DBConnector.getVisitedCheckpoints(student)

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
        }

        return sb.append("]").toString()
    }
}