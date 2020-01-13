package de.kos.sport.routes.student

import de.kos.sport.database.Class
import de.kos.sport.database.Classes
import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class StudentCreateRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")

        val token = req.splat()[0]
        val classId = req.splat()[1].toIntOrNull()
        val studentCount: Int? = req.splat()[2].toIntOrNull()

        if (DBConnector.validateToken(token)) {
            val adminUser = transaction { DBConnector.getSessionFromToken(token)!!.user } //User is never null at this point

            if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                if (studentCount == null) {
                    sb.append("{ \"error\": \"Count needs to be an integer\" }")
                } else if (classId == null) {
                    sb.append("{ \"error\": \"Id needs to be an integer\" }")
                } else {
                    val clazz = transaction { Class.find { Classes.id eq classId }.firstOrNull() }

                    if (clazz != null) {
                        sb.append("{ \"students\": [")
                        for (i in 1..studentCount) {
                            val student = DBConnector.createStudent(clazz)

                            sb.append(student.studentId)

                            if (i < studentCount) {
                                sb.append(", ")
                            }
                        }
                        sb.append("] }")
                    } else {
                        sb.append("{ \"error\": \"Class not found\" }")
                    }
                }
            }
        } else {
            sb.append("{ \"error\": \"Invalid Token\" }")
        }

        return sb.append("]").toString()
    }
}
