package de.kos.sport.routes.user

import de.kos.sport.database.DBConnector
import spark.Request
import spark.Response
import spark.Route

class UserRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val id = req.params(":id").toIntOrNull()

        if (id == null) {
            sb.append("{ \"error\": \"Id needs to be an integer\" }")
        } else {
            val user = DBConnector.getUserById(id)

            if (user != null) {
                sb.append(user)
            } else {
                sb.append("{ \"error\": \"User not found\" }")
            }
        }

        return sb.append("]").toString()
    }
}
