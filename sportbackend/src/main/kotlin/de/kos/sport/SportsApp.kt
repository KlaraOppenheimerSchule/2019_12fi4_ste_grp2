package de.kos.sport

import de.kos.sport.database.DBConnector
import spark.Spark

object SportsApp {
    private const val SPARK_PORT = 8181

    /**
     * Starts the spark web server and configures it to provide the api endpoints
     */
    fun start() {
        //Init DBConnector at start before doing anything else
        DBConnector.init()

        //Reconfigure spark to use another port
        Spark.port(SPARK_PORT)

        //Define api endpoints
        Spark.get("/api") { req, res ->
            Spark.get("/stats") { req, res ->
                //Toplist
                Spark.get("/top") { req, res ->
                    //Amount of students
                    Spark.get("/student/:count") { req, res ->
                    }
                    //Amount of classes
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
        }
    }
}
