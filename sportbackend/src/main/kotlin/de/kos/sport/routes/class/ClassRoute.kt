package de.kos.sport.routes.`class`

import de.kos.sport.database.DBConnector
import spark.Request
import spark.Response
import spark.Route

class ClassRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val id = req.params(":id").toIntOrNull()

        if (id != null) {
            val clazz = DBConnector.getClassById(id)

            if (clazz != null) {
                sb.append(clazz)
            }
        } else {
            sb.append("{ \"error\": \"Id needs to be an integer\" }")
        }

        return sb.append("]").toString()
    }
}
