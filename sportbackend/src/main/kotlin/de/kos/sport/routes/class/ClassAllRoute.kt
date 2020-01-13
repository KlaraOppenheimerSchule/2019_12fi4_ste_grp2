package de.kos.sport.routes.`class`

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class ClassAllRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val token = req.params(":token")

        try {
            if (DBConnector.validateToken(token)) {
                val adminUser = transaction { DBConnector.getSessionFromToken(token)!!.user } //User is never null at this point

                if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                    val classes = transaction { de.kos.sport.database.Class.all() }

                    transaction {
                        classes.forEachIndexed { id, clazz ->
                            sb.append(clazz.toLightweightString())

                            if (id < classes.count() - 1) {
                                sb.append(", ")
                            }
                        }
                    }
                } else {
                    sb.append("{ \"error\": \"Insufficient permissions\" }")
                }
            } else {
                sb.append("{ \"error\": \"Invalid Token\" }")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return sb.append("]").toString()
    }
}
