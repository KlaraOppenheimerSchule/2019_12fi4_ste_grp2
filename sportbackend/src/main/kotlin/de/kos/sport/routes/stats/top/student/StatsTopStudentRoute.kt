package de.kos.sport.routes.stats.top.student

import de.kos.sport.database.Student
import de.kos.sport.database.Students
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class StatsTopStudentRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")

        val count: Int? = req.params(":count").toIntOrNull()

        if (count == null) {
            sb.append("{ \"error\": \"Count needs to be an integer\" }")
        } else {
            transaction {
                val students = Student.all()
                    .orderBy(Students.score to SortOrder.DESC)
                    .limit(count)
                students.forEachIndexed { i, it ->
                    sb.append(it.toString())
                    if (i < students.count() - 1) {
                        sb.append(", ")
                    }
                }
            }
        }

        sb.append("]")

        return sb.toString()
    }
}
