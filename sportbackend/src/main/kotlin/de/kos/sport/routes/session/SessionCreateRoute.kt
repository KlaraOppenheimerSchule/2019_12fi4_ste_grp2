package de.kos.sport.routes.session

import de.kos.sport.database.DBConnector
import de.kos.sport.database.User
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class SessionCreateRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")

        val username = req.splat()[0]
        val password = req.splat()[1]

        val user = transaction { User.all().find { it.name == username } }

        if (user != null) {
            if (user.password == password) {
                val session = DBConnector.createSession(user)
                sb.append("{ \"token\": \"${session.id}\", \"type\": \"${user.type}\" }")
            } else {
                sb.append("{ \"error\": \"Invalid password\" }")
            }
        } else {
            sb.append("{ \"error\": \"User not found\" }")
        }

        return sb.append("]").toString()
    }
}
