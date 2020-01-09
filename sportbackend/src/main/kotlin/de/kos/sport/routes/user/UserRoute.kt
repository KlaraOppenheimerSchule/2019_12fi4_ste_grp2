package de.kos.sport.routes.user

import de.kos.sport.database.User
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class UserRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder().append("[")
        val id = req.params(":id").toIntOrNull()

        val user = transaction { User.all().find { it.id.value == id } }

        if (user != null) {
            sb.append(user)
        } else {
            sb.append("{ \"error\": \"User not found\" }")
        }

        return sb.append("]").toString()
    }
}