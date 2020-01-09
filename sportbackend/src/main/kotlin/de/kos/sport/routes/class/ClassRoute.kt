package de.kos.sport.routes.`class`

import de.kos.sport.database.Student
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class ClassRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val id = req.params(":id").toIntOrNull()
        val sb = StringBuilder().append("[")

        val student = transaction { Student.all().find { it.studentId == id } }

        if (student != null) {
            transaction {
                sb.append(student.clazz)
            }
        } else {
            sb.append("{ \"error\": \"Student not found\" }")
        }

        return sb.append("]").toString()
    }
}