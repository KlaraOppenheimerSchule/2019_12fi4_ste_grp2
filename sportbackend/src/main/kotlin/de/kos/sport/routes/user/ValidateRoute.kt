package de.kos.sport.routes.user

import de.kos.sport.database.DBConnector
import spark.Request
import spark.Response
import spark.Route

class ValidateRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder().append("[")
        val token = req.params(":token")

        val user = DBConnector.getUserFromToken(token)

        if (user != null) {
            sb.append("{ \"user\": ${user.id}, \"valid\": ${DBConnector.validateToken(token)} }")
        } else {
            sb.append("{ \"valid\": false }")
        }

        return sb.append("]").toString()
    }
}
