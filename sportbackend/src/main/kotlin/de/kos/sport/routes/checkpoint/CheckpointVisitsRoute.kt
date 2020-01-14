package de.kos.sport.routes.checkpoint

import de.kos.sport.database.DBConnector
import de.kos.sport.database.VisitedCheckpoint
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class CheckpointVisitsRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val id = req.params(":id").toIntOrNull()
        val token = req.splat()[0]

        if (DBConnector.validateToken(token)) {
            val adminUser =
                transaction { DBConnector.getSessionFromToken(token)!!.user }

            if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                if (id == null) {
                    sb.append("{ \"error\": \"Id needs to be an integer\" }")
                } else {
                    transaction {
                        val visits = VisitedCheckpoint.all().filter { it.checkpoint.id.value == id }
                        visits.forEachIndexed { id, visit ->
                            sb.append(visit)

                            if (id < visits.count() - 1) {
                                sb.append(", ")
                            }
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