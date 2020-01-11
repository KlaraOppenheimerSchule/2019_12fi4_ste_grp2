package de.kos.sport.routes.session

import de.kos.sport.database.DBConnector
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class DestroyRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        try {
            val token = req.params(":token")

            val session = DBConnector.getSessionFromToken(token)

            if (session != null) {
                transaction {
                    session.delete()
                }
                sb.append("{ \"success\": ${!DBConnector.validateSession(session)}")
            } else {
                sb.append("{ \"error\": \"Invalid Token\" }")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return sb.append("]").toString()
    }
}