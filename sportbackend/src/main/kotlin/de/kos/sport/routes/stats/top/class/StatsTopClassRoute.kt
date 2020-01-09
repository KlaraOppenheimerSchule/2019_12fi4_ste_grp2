package de.kos.sport.routes.stats.top.`class`

import de.kos.sport.database.Class
import de.kos.sport.database.Classes
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class StatsTopClassRoute : Route {
    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder().append("[")
        val count: Int? = req.params(":count").toIntOrNull()

        if (count == null) {
            sb.append("{ \"error\": \"Count needs to be an integer\" }")
        } else {
            val classes = transaction {
                Class.all()
                    .orderBy(Classes.score to SortOrder.DESC)
                    .limit(count)
            }

            transaction {
                classes.forEachIndexed { i, it ->
                    sb.append(it.toString())
                    if (i < classes.count() - 1) {
                        sb.append(", ")
                    }
                }
            }
        }

        sb.append("]")

        return sb.toString()
    }
}
