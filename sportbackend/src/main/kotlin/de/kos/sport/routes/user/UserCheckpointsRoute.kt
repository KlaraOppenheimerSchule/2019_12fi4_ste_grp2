package de.kos.sport.routes.user

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class UserCheckpointsRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val userId = req.params(":id").toIntOrNull()
        val token = req.splat()[0]

        if (userId == null) {
            sb.append("{ \"error\": \"Id needs to be an integer\" }")
        } else if (DBConnector.validateToken(token)) {
            val adminUser = transaction { DBConnector.getSessionFromToken(token)!!.user }

            if (adminUser.type >= DBConnector.ACCESS_LEVEL_CHECKPOINT) {
                val user = DBConnector.getUserById(userId)

                if (user == null) {
                    sb.append("{ \"error\": \"User not found\" }")
                } else {
                    val checkpoints = DBConnector.getCheckpointsByUser(user)

                    if (checkpoints.isEmpty()) {
                        sb.append("{ \"error\": \"User has no checkpoints\" }")
                    } else {
                        sb.append("{ \"user\": ${user.id.value}, \"checkpoints\": [")

                        checkpoints.forEachIndexed { id, checkpoint ->
                            sb.append(checkpoint)

                            if (id < checkpoints.count() - 1) {
                                sb.append(", ")
                            }
                        }

                        sb.append("] }")
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