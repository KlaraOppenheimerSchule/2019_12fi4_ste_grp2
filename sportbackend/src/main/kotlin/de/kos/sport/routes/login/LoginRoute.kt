package de.kos.sport.routes.login

import de.kos.sport.database.DBConnector
import de.kos.sport.database.User
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route
import java.util.*

class LoginRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder().append("[")
        val username = req.splat()[0]
        val password = req.splat()[1]

        val user = transaction { User.all().find { it.name == username } }

        if (user != null) {
            if (user.password == password) {
                val token = UUID.randomUUID().toString().replace("-", "")
                DBConnector.refreshToken(user, token)
                sb.append("{ \"token\": \"$token\", \"type\": \"${user.type}\" }")
            } else {
                sb.append("{ \"error\": \"Invalid password\" }")
            }
        } else {
            sb.append("{ \"error\": \"User not found\" }")
        }

        return sb.append("]").toString()
    }
}