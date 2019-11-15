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
            Spark.path("/stats") {
                Spark.get("/top/class/:count") { req, res ->
                    val sb = StringBuilder().append("[")
                    val count = req.params(":count").toInt()

                    val classes = Class.all()
                        .orderBy(Classes.score to SortOrder.DESC)
                        .limit(count)

                    classes.forEachIndexed { i, it ->
                        sb.append(it.toString())
                        if (i < classes.count() - 1) {
                            sb.append(", ")
                        }
                    }

                    sb.toString()
                }
                Spark.get("/student/:id") { req, res ->
                    val sb = StringBuilder().append("[")
                    val id = req.params(":id").toInt()

                    transaction {
                        val student = Student.find { Students.studentId eq id }.firstOrNull()
                        val students = Student.all().orderBy(Students.score to SortOrder.DESC).toList()

                        val studentIndex = students.indexOf(student)


                        if (student != null) {
                            if (students.size > 1) {
                                val betterStudent = students[studentIndex - 1]
                                sb.append(betterStudent.toString()).append(", ")
                            }

                            sb.append(student.toString())

                            if (students.size <= studentIndex + 1) {
                                sb.append(", ")
                                val worseStudent = students[studentIndex + 1]
                                sb.append(worseStudent.toString())
                            }

                            "" //Why?
                        } else {
                            sb.append("{ \"error\": \"Student not found\" }")
                        }
                    }
                    sb.append("]")

                    sb.toString()
                }
                Spark.get("/class/:id") { req, res ->
                    val sb = StringBuilder().append("[")
                    val id = req.params(":id").toInt()

                    transaction {
                        val student = Student.find { Students.studentId eq id }.firstOrNull()

                        if (student != null) {

                        } else {
                            sb.append("{ \"error\": \"Student not found\" }")
                        }
                    }
                    sb.append("]")

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

        //Define api endpoints
       /* Spark.path("/api") {
            Spark.path("/stats") {
                //Toplist
                Spark.get("/top/:count") { req, res ->
                    val count = req.params(":count").toInt()

                    Spark.get("/student/:count") { req, res ->
                        logger.debug { "Incoming request to ${req.contextPath()}" }

                        val sb = StringBuilder().appendln("[")

                        transaction {
                            Student.all()
                                .orderBy(Students.score to SortOrder.DESC)
                                .limit(count)
                                .forEachIndexed { i, it ->
                                    sb.append(it)
                                    if (i < count) {
                                        sb.append(",")
                                    }
                                    sb.appendln()
                                }
                        }

                        sb.appendln("]")

                        res.body(sb.toString())
                    }

                    Spark.get("/class/:count") { req, res ->
                    }
                }

                //Personal stats StudentId
                Spark.get("/student/:id") { req, res ->

                }

                //Class stats StudentId
                Spark.get("/class/:id") { req, res ->
                }
            }

            //StudentId
            Spark.get("/class/:id") { req, res ->
            }
        }*/
    }
}
