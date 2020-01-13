package de.kos.sport.routes.checkpoint

import de.kos.sport.database.DBConnector
import spark.Request
import spark.Response
import spark.Route

class CheckpointRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val id = req.params(":id").toIntOrNull()

        if (id != null) {
            val checkpoint = DBConnector.getCheckpointById(id)

            if (checkpoint != null) {
                sb.append(checkpoint)
            }
        } else {
            sb.append("{ \"error\": \"Id needs to be an integer\" }")
        }

        return sb.append("]").toString()
    }
}