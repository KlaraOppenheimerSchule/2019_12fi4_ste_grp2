package de.kos.sport.routes.user

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class UserDeleteRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val userId = req.params(":id").toIntOrNull()
        val token = req.splat()[0]

        if (userId == null) {
            sb.append("{ \"error\": \"Id needs to be an integer\" }")
        } else if (DBConnector.validateToken(token)) {
            val adminUser = transaction { DBConnector.getSessionFromToken(token)!!.user }

            if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                val user = DBConnector.getUserById(userId)

                if (user == null) {
                    sb.append("{ \"error\": \"User not found\" }")
                } else {
                    transaction {
                        user.delete()
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