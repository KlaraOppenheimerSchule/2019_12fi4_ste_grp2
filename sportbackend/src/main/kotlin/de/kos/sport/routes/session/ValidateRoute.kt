package de.kos.sport.routes.session

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class ValidateRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val token = req.params(":token")

        val session = DBConnector.getSessionFromToken(token)

        if (session != null) {
            sb.append("{ \"user\": ${transaction { session.user.id }}, \"valid\": ${DBConnector.validateSession(session)} }")
        } else {
            sb.append("{ \"valid\": false }")
        }

        return sb.append("]").toString()
    }
}
