package de.kos.sport.routes.session

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class DestroyRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val token = req.params(":token")

        val session = DBConnector.getSessionFromToken(token)

        if (session != null) {
            transaction {
                session.delete()
            }
            //Use validate token to obtain a new session handle with updated values or null
            sb.append("{ \"success\": ${DBConnector.validateToken(token)} }")
        } else {
            sb.append("{ \"error\": \"Invalid Token\" }")
        }

        return sb.append("]").toString()
    }
}
