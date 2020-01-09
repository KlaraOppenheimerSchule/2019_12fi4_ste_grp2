package de.kos.sport.routes.stats.`class`

import de.kos.sport.database.Class
import de.kos.sport.database.Classes
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class StatsClassRoute : Route {
    companion object {
        /**
         *  The amount of classes to sample
         */
        private const val SAMPLE_LIMIT = 3
    }

    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder().append("[")

        val id = req.params(":id").toIntOrNull()

        val classes = transaction { Class.all().orderBy(Classes.score to SortOrder.DESC).toList() }

        val clazz = classes.find { transaction { it.students.find { it.studentId == id } != null } }

        if (clazz != null) {
            val classIndex = classes.indexOf(clazz)
            val classIterator1 = classes.listIterator(classIndex)
            val classIterator2 = classes.listIterator(classIndex)

            var count = 1

            if (classIterator2.hasNext()) {
                classIterator2.next()
            }

            if (classIterator1.hasPrevious() && count < SAMPLE_LIMIT) {
                val prev2 = classIterator1.previous()
                var prev1: Class? = null

                count++
                if (!classIterator2.hasNext() && classIterator1.hasPrevious() && count < SAMPLE_LIMIT) {
                    prev1 = classIterator1.previous()
                    count++
                }

                if (prev1 != null) {
                    sb.append(prev1).append(", ")
                }

                sb.append(prev2).append(", ")
            }

            sb.append(clazz)

            if (classIterator2.hasNext() && count < SAMPLE_LIMIT) {
                sb.append(", ").append(classIterator2.next())
                count++

                if (classIterator2.hasNext() && count < SAMPLE_LIMIT) {
                    sb.append(", ").append(classIterator2.next())
                }
            }

            sb.append("]")
        } else {
            sb.append("{ \"error\": \"Student not found\" }")
        }

        return sb.toString()
    }
}
