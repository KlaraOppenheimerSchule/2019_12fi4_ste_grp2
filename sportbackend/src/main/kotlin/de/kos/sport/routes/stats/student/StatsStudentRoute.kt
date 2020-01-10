package de.kos.sport.routes.stats.student

import de.kos.sport.database.Student
import de.kos.sport.database.Students
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Request
import spark.Response
import spark.Route

class StatsStudentRoute : Route {
    companion object {
        /**
         *  The amount of classes to sample
         */
        private const val SAMPLE_LIMIT = 3
    }

    override fun handle(req: Request, response: Response): Any {
        val sb = StringBuilder("[")
        val id = req.params(":id").toIntOrNull()

        val students = transaction { Student.all().orderBy(Students.score to SortOrder.DESC).toList() }
        val student = students.find { it.studentId == id }

        if (student != null) {
            val studentIndex = students.indexOf(student)
            val studentIterator1 = students.listIterator(studentIndex)
            val studentIterator2 = students.listIterator(studentIndex)

            var count = 1

            if (studentIterator2.hasNext()) {
                studentIterator2.next()
            }

            if (studentIterator1.hasPrevious() && count < SAMPLE_LIMIT) {
                val prev2 = studentIterator1.previous()
                var prev1: Student? = null

                count++
                if (!studentIterator2.hasNext() && studentIterator1.hasPrevious() && count < SAMPLE_LIMIT) {
                    prev1 = studentIterator1.previous()
                    count++
                }

                if (prev1 != null) {
                    sb.append(prev1).append(", ")
                }

                sb.append(prev2).append(", ")
            }

            sb.append(student)

            if (studentIterator2.hasNext() && count < SAMPLE_LIMIT) {
                sb.append(", ").append(studentIterator2.next())
                count++

                if (studentIterator2.hasNext() && count < SAMPLE_LIMIT) {
                    sb.append(", ").append(studentIterator2.next())
                }
            }

            sb.append("]")
        } else {
            sb.append("{ \"error\": \"Student not found\" }")
        }

        return sb.toString()
    }
}
