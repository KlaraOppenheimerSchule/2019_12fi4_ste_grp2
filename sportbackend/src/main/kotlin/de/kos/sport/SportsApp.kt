package de.kos.sport

import de.kos.sport.database.DBConnector
import de.kos.sport.database.Student
import de.kos.sport.database.Students
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Spark
import de.kos.sport.database.*

object SportsApp {

    val logger = KotlinLogging.logger("SportsApp")

    private const val SPARK_PORT = 8181

    /**
     * Starts the spark web server and configures it to provide the api endpoints
     */
    fun start() {
        logger.info { "Starting SportsApp" }
        logger.debug { "Initing DBConnector" }

        //Init DBConnector at start before doing anything else
        DBConnector.init()

        logger.debug { "DBConnector inited" }

        //Reconfigure spark to use another port
        Spark.port(SPARK_PORT)

        logger.debug { "Spark is listening to port $SPARK_PORT" }
        logger.info { "SportsApp started" }

        Spark.path("/api") {
            Spark.path("/class") {
                Spark.get("/:id") { req, res ->
                    val id = req.params(":id").toInt()
                    val sb = StringBuilder().append("[")

                    val student = transaction { Student.all().find { it.studentId == id } }

                    if (student != null) {
                        sb.append(student)
                    } else {
                        sb.append("{ \"error\": \"Student not found\" }")
                    }

                    sb.append("]").toString()
                }
            }
            Spark.path("/stats") {
                Spark.get("/top/class/:count") { req, res ->
                    val sb = StringBuilder().append("[")
                    val count = req.params(":count").toInt()

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

                    sb.toString()
                }
                Spark.get("/student/:id") { req, res ->
                    val sb = StringBuilder()
                    val id = req.params(":id").toInt()

                    val students = transaction { Student.all().orderBy(Students.score to SortOrder.DESC).toList() }
                    val student = students.find { it.studentId == id }

                    if (student != null) {
                        val studentIndex = students.indexOf(student)
                        val studentIterator1 = students.listIterator(studentIndex)
                        val studentIterator2 = students.listIterator(studentIndex)

                        val limit = 3
                        var count = 1

                        if (studentIterator2.hasNext()) {
                            studentIterator2.next()
                        }

                        sb.append("[")

                        if (studentIterator1.hasPrevious() && count < limit) {
                            val prev2 = studentIterator1.previous()
                            var prev1: Student? = null

                            count++
                            if (!studentIterator2.hasNext() && studentIterator1.hasPrevious() && count < limit) {
                                prev1 = studentIterator1.previous()
                                count++
                            }

                            if (prev1 != null) {
                                sb.append(prev1).append(", ")
                            }

                            sb.append(prev2).append(", ")
                        }

                        sb.append(student)

                        if (studentIterator2.hasNext() && count < limit) {
                            sb.append(", ").append(studentIterator2.next())
                            count++

                            if (studentIterator2.hasNext() && count < limit) {
                                sb.append(", ").append(studentIterator2.next())
                            }
                        }

                        sb.append("]")
                    } else {
                        sb.append("{ \"error\": \"Student not found\" }")
                    }

                    sb.toString()
                }
                Spark.get("/class/:id") { req, res ->
                    val sb = StringBuilder()

                    val id = req.params(":id").toInt()

                    val classes = transaction { Class.all().orderBy(Classes.score to SortOrder.DESC).toList() }

                    val clazz = classes.find { transaction { it.students.find { it.studentId == id } != null } }

                    if (clazz != null) {
                        val classIndex = classes.indexOf(clazz)
                        val classIterator1 = classes.listIterator(classIndex)
                        val classIterator2 = classes.listIterator(classIndex)

                        val limit = 3
                        var count = 1

                        if (classIterator2.hasNext()) {
                            classIterator2.next()
                        }

                        sb.append("[")

                        if (classIterator1.hasPrevious() && count < limit) {
                            val prev2 = classIterator1.previous()
                            var prev1: Class? = null

                            count++
                            if (!classIterator2.hasNext() && classIterator1.hasPrevious() && count < limit) {
                                prev1 = classIterator1.previous()
                                count++
                            }

                            if (prev1 != null) {
                                sb.append(prev1).append(", ")
                            }

                            sb.append(prev2).append(", ")
                        }

                        sb.append(clazz)

                        if (classIterator2.hasNext() && count < limit) {
                            sb.append(", ").append(classIterator2.next())
                            count++

                            if (classIterator2.hasNext() && count < limit) {
                                sb.append(", ").append(classIterator2.next())
                            }
                        }

                        sb.append("]")
                    } else {
                        sb.append("{ \"error\": \"Student not found\" }")
                    }
                    sb.toString()
                }
                Spark.get("/top/student/:count") { req, res ->
                    val count = req.params(":count").toInt()

                    val sb = StringBuilder().append("[")

                    transaction {
                        val students = Student.all()
                            .orderBy(Students.score to SortOrder.DESC)
                            .limit(count)
                        students.forEachIndexed { i, it ->
                            sb.append(it.toString())
                            if (i < students.count() - 1) {
                                sb.append(", ")
                            }
                        }
                    }

                    sb.append("]")

                    sb.toString()
                }
            }
        }
    }
}

/* UNUSED
 val sb = StringBuilder()

                    val id = req.params(":id").toInt()

                    val student = transaction { Student.find { Students.studentId eq id }.firstOrNull() }

                    if (student != null) {
                        val students = transaction {
                            Student.wrapRows(
                                Students.select { Students.studentClass eq student.clazz.id }.orderBy(
                                    Students.score to SortOrder.DESC
                                )
                            ).toList()
                        }

                        val studentIndex = students.indexOf(students.find { it.studentId == id })
                        val studentIterator1 = students.listIterator(studentIndex)
                        val studentIterator2 = students.listIterator(studentIndex)

                        val limit = 3
                        var count = 1

                        if (studentIterator2.hasNext()) {
                            studentIterator2.next()
                        }

                        sb.append("[")

                        if (studentIterator1.hasPrevious() && count < limit) {
                            sb.append(studentIterator1.previous()).append(", ")

                            count++
                            if (!studentIterator2.hasNext() && studentIterator1.hasPrevious() && count < limit) {
                                sb.append(studentIterator1.previous()).append(", ")
                                count++
                            }
                        }

                        sb.append(student)

                        if (studentIterator2.hasNext() && count < limit) {
                            sb.append(", ").append(studentIterator2.next())
                            count++

                            if (studentIterator2.hasNext() && count < limit) {
                                sb.append(studentIterator2.next())
                            }
                        }

                        sb.append("]")

                    } else {
                        sb.append("{ \"error\": \"Student not found\" }")
                    }

                    sb.toString()
 */
