package de.kos.sport.routes.`class`

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route
import java.sql.SQLException

class ClassCreateRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")

        val token = req.splat()[0]
        val className = req.splat()[1]

        if (DBConnector.validateToken(token)) {
            val adminUser = transaction { DBConnector.getSessionFromToken(token)!!.user }

            if (adminUser.type == DBConnector.ACCESS_LEVEL_GLOBAL) {
                try {
                    val generatedClass = DBConnector.createClass(className)
                    sb.append("{ \"class\": ${generatedClass.id} }")
                } catch (ex: SQLException) {
                    //The best way at the moment to detect if a user already exists when the query fails
                    //without forcing an additional sql statement
                    sb.append("{ \"error\": \"Class already exists\" }")
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
