package de.kos.sport

import com.google.gson.GsonBuilder
import de.kos.sport.database.DBConnector
import de.kos.sport.database.Student
import de.kos.sport.database.Students
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Spark

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

        //Define api endpoints
        Spark.get("/api") { req, res ->
            Spark.get("/stats") { req, res ->
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

            "Hello :)"
        }
    }
}
