package de.kos.sport.routes.checkpoint

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class CheckpointDeleteRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val checkpointId = req.params(":id").toIntOrNull()
        val token = req.splat()[0]

        if (checkpointId == null) {
            sb.append("{ \"error\": \"Id needs to be an integer\" }")
        } else if (DBConnector.validateToken(token)) {
            val adminUser = transaction { DBConnector.getSessionFromToken(token)!!.user }

            if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                val checkpoint = DBConnector.getCheckpointById(checkpointId)

                if (checkpoint == null) {
                    sb.append("{ \"error\": \"Checkpoint not found\" }")
                } else {
                    transaction {
                        checkpoint.delete()
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