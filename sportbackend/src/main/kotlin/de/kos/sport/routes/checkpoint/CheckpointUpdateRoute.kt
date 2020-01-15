package de.kos.sport.routes.checkpoint

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class CheckpointUpdateRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val checkpointId = req.params(":id").toIntOrNull()

        val token = req.splat()[0]

        val name = req.splat()[1]
        val location = req.splat()[2]
        val userId = req.splat()[3].toIntOrNull()
        val score = req.splat()[4].toIntOrNull()

        if (DBConnector.validateToken(token)) {
            val adminUser =
                transaction { DBConnector.getSessionFromToken(token)!!.user }

            if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                if (score == null) {
                    sb.append("{ \"error\": \"Score needs to be an integer\" }")
                } else if (userId == null) {
                    sb.append("{ \"error\": \"User needs to be an integer\" }")
                } else if (checkpointId == null) {
                    sb.append("{ \"error\": \"Id needs to be an integer\" }")
                } else {
                    val user = DBConnector.getUserById(userId)

                    if (user == null) {
                        sb.append("{ \"error\": \"User not found\" }")
                    } else {
                        val checkpoint = DBConnector.getCheckpointById(checkpointId)

                        if (checkpoint == null) {
                            sb.append("{ \"error\": \"Checkpoint not found\" }")
                        } else {

                            transaction {
                                checkpoint.name = name
                                checkpoint.location = location
                                checkpoint.user = user
                                checkpoint.score = score
                            }
                        }
                    }
                }
            } else {
                sb.append("{ \"error\": \"Insufficient permissions\" }")
            }
        }

        return sb.append("]").toString()
    }
}