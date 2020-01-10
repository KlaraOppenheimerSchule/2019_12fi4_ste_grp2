package de.kos.sport.routes.`class`

import de.kos.sport.database.DBConnector
import spark.Request
import spark.Response
import spark.Route

class ClassCreateRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")

        val token = req.splat()[0]
        val className = req.splat()[1]

        if (DBConnector.validateToken(token)) {
            val generatedClass = DBConnector.createClass(className)
            sb.append("{ \"class\": ${generatedClass.id} }")
        } else {
            sb.append("{ \"error\": \"Token invalid\" }")
        }

        return sb.append("]").toString()
    }
}
