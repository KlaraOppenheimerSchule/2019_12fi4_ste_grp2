package de.kos.sport.routes.student

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class StudentPresentRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val token = req.splat()[0]
        val studentId = req.splat()[1].toIntOrNull()
        val present = "true" == req.splat()[2].orEmpty().toLowerCase()

        if (DBConnector.validateToken(token)) {
            val adminUser = DBConnector.getSessionFromToken(token)!!.user //User is never null at this point

            if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                if (studentId == null) {
                    sb.append("{ \"error\": \"Id needs to be an integer\" }")
                } else {
                    val student =
                        DBConnector.getStudentByStudentId(studentId)

                    if (student != null) {
                        transaction {
                            student.present = present
                        }
                        sb.append(student)
                    } else {
                        sb.append("{ \"error\": \"Student not found\" }")
                    }
                }
            } else {
                sb.append("{ \"error\": \"Insufficient permissions\" }")
            }
        } else {
            sb.append("{ \"error\": \"Invalid Token\" }")
        }

        return sb.append("]").toString()
    }
}
