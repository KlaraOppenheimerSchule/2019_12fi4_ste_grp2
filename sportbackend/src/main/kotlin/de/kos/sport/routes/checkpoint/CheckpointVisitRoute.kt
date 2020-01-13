package de.kos.sport.routes.checkpoint

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class CheckpointVisitRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")

        val id = req.params(":id").toIntOrNull()
        val token = req.splat()[0]
        val studentId = req.splat()[1].toIntOrNull()

        if (DBConnector.validateToken(token)) {
            val adminUser =
                transaction { DBConnector.getSessionFromToken(token)!!.user }

            if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                if (id == null) {
                    sb.append("{ \"error\": \"Id needs to be an integer\" }")
                } else if (studentId == null) {
                    sb.append("{ \"error\": \"Student id needs to be an integer\" }")
                } else {
                    val checkpoint = DBConnector.getCheckpointById(id)
                    val student = DBConnector.getStudentByStudentId(studentId)

                    when {
                        checkpoint == null -> {
                            sb.append("{ \"error\": \"Invalid checkpoint\" }")
                        }
                        student == null -> {
                            sb.append("{ \"error\": \"Invalid user\" }")
                        }
                        else -> {
                            DBConnector.visitCheckpoint(student, checkpoint)
                        }
                    }
                }
            }
        } else {
            sb.append("{ \"error\": \"Invalid Token\" }")
        }

        return sb.append("]").toString()
    }
}