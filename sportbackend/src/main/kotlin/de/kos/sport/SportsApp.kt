package de.kos.sport

import de.kos.sport.database.DBConnector
import de.kos.sport.database.Student
import de.kos.sport.database.Students
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Spark
import de.kos.sport.database.*
import org.jetbrains.exposed.sql.select
import java.lang.Exception
import java.util.*

object SportsApp {

    //TODO Outsource endpoint handlers to external classes
    //TODO Use json library to build json responses
    //TODO Instead of using "Table.all()" create a proper select statement

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
            Spark.get("/user/:id") { req, res ->
                val sb = StringBuilder()
                val id = req.params(":id").toIntOrNull()

                val user = transaction { User.all().find { it.id.value == id } }

                sb.append("[")
                if (user != null) {
                    sb.append(user)
                } else {
                    sb.append("{ \"error\": \"User not found\" }")
                }
                sb.append("]")

                sb.toString()
            }
            Spark.get("/validate/:token") { req, res ->
                val sb = StringBuilder()
                val token = req.params(":token")

                val user = DBConnector.getUserFromToken(token)

                sb.append("[")
                if (user != null) {
                    sb.append("{ \"user\": ${user.id}, \"valid\": ${DBConnector.validateToken(token)} }")
                } else {
                    sb.append("{ \"valid\": false }")
                }
                sb.append("]")

                sb.toString()
            }
            Spark.path("/login") {
                Spark.get("/*/*") { req, res ->
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

                    sb.append("]")
                    sb.toString()
                }
            }
            Spark.path("/class") {
                Spark.get("/create/*/*") { req, res ->
                    val sb = StringBuilder().append("[")

                    val token = req.splat()[0]
                    val className = req.splat()[1]

                    if (DBConnector.validateToken(token)) {
                        val generatedClass = DBConnector.createClass(className)
                        sb.append("{ \"class\": ${generatedClass.id} }")
                    } else {
                        printInvalidTokenMessage(sb)
                    }

                    sb.append("]").toString()
                }
                Spark.get("/:id") { req, res ->
                    val id = req.params(":id").toIntOrNull()
                    val sb = StringBuilder().append("[")


                    val student = transaction { Student.all().find { it.studentId == id } }

                    if (student != null) {
                        transaction {
                            sb.append(student.clazz)
                        }
                    } else {
                        sb.append("{ \"error\": \"Student not found\" }")
                    }

                    sb.append("]").toString()
                }
            }
            Spark.path("/student") {
                Spark.get("/create/*/*/*") { req, res ->
                    try {
                        val sb = StringBuilder().append("[")

                        val token = req.splat()[0]
                        val classId = req.splat()[1].toIntOrNull()
                        val studentCount: Int? = req.splat()[2].toIntOrNull()

                        if (studentCount == null) {
                            sb.append("{ \"error\": \"Count needs to be an integer\" }")
                        } else if (DBConnector.validateToken(token)) {
                            val clazz = transaction { Class.find { Classes.id eq classId }.firstOrNull() }
                            if (clazz != null) {
                                sb.append("{ \"students\": [")
                                for (i in 1..studentCount) {
                                    val student = DBConnector.createStudent(clazz)

                                    sb.append(student.studentId)

                                    if (i < studentCount) {
                                        sb.append(", ")
                                    }
                                }
                                sb.append("] }")
                            } else {
                                sb.append("{ \"error\": \"Class not found\" }")
                            }
                        } else {
                            printInvalidTokenMessage(sb)
                        }
                        sb.append("]").toString()
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }

                }
            }
            Spark.path("/stats") {
                Spark.get("/top/class/:count") { req, res ->
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

                    sb.toString()
                }
                Spark.get("/student/:id") { req, res ->
                    val sb = StringBuilder()
                    val id = req.params(":id").toIntOrNull()

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

                    val id = req.params(":id").toIntOrNull()

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
                    val count: Int? = req.params(":count").toIntOrNull()

                    val sb = StringBuilder().append("[")

                    if (count == null) {
                        sb.append("{ \"error\": \"Count needs to be an integer\" }")
                    } else {
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
                    }

                    sb.append("]")

                    sb.toString()
                }
            }
        }
    }

    fun printInvalidTokenMessage(sb: StringBuilder) {
        sb.append("{ \"error\": \"Token invalid\" }")
    }
}
