package de.kos.sport.routes.`class`

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class ClassDeleteRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")

        val token = req.splat()[0]
        val classId = req.params(":id").toIntOrNull()

        if (classId == null) {
            sb.append("{ \"error\": \"Id needs to be an integer\" }")
        } else {
            if (DBConnector.validateToken(token)) {
                val adminUser = transaction { DBConnector.getSessionFromToken(token)!!.user }

                if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                    val clazz = DBConnector.getClassById(classId)

                    if (clazz == null) {
                        sb.append("{ \"error\": \"Class not found\" }")
                    } else {
                        transaction {
                            clazz.delete()
                        }
                    }
                } else {
                    sb.append("{ \"error\": \"Insufficient permissions\" }")
                }
            } else {
                sb.append("{ \"error\": \"Invalid Token\" }")
            }
        }

        return sb.append("]").toString()
    }
}