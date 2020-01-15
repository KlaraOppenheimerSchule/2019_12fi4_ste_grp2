package de.kos.sport.routes.user

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class UserUpdateRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val userId = req.params(":id").toIntOrNull()
        val token = req.splat()[0]
        val password = req.splat()[1].orEmpty()

        if (userId == null) {
            sb.append("{ \"error\": \"Id needs to be an integer\" }")
        } else if (password.isEmpty()) {
            sb.append("{ \"error\": \"Password cant be empty\" }")
        } else if (DBConnector.validateToken(token)) {
            val adminUser = transaction { DBConnector.getSessionFromToken(token)!!.user }

            if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                val user = DBConnector.getUserById(userId)

                if (user == null) {
                    sb.append("{ \"error\": \"User not found\" }")
                } else {
                    transaction {
                        user.password = password
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