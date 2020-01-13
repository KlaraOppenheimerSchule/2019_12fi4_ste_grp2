package de.kos.sport.routes.checkpoint

import de.kos.sport.database.Checkpoint
import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class CheckpointAllRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val token = req.params(":token")

        if (DBConnector.validateToken(token)) {
            val adminUser =
                transaction { DBConnector.getSessionFromToken(token)!!.user }

            if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                val checkpoints = transaction { Checkpoint.all() }

                transaction {
                    checkpoints.forEachIndexed { id, checkpoint ->
                        sb.append(checkpoint)

                        if (id < checkpoints.count() - 1) {
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

        return sb.append("]").toString()
    }
}