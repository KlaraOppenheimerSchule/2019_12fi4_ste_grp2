package de.kos.sport

import de.kos.sport.database.DBConnector
import spark.Spark

object SportsApp {

    fun start() {
        DBConnector.init()

        Spark.port(8181)
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