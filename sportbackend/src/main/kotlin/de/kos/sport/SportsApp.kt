package de.kos.sport

import de.kos.sport.database.DBConnector
import de.kos.sport.routes.`class`.ClassAllRoute
import de.kos.sport.routes.`class`.ClassCreateRoute
import de.kos.sport.routes.`class`.ClassRoute
import de.kos.sport.routes.checkpoint.CheckpointCreateRoute
import de.kos.sport.routes.checkpoint.CheckpointRoute
import de.kos.sport.routes.checkpoint.CheckpointVisitRoute
import de.kos.sport.routes.session.SessionCreateRoute
import de.kos.sport.routes.session.SessionDestroyRoute
import de.kos.sport.routes.session.SessionValidateRoute
import de.kos.sport.routes.stats.`class`.StatsClassRoute
import de.kos.sport.routes.stats.student.StatsStudentRoute
import de.kos.sport.routes.stats.top.`class`.StatsTopClassRoute
import de.kos.sport.routes.stats.top.student.StatsTopStudentRoute
import de.kos.sport.routes.student.StudentClassRoute
import de.kos.sport.routes.student.StudentCreateRoute
import de.kos.sport.routes.student.StudentPresentRoute
import de.kos.sport.routes.student.StudentRoute
import de.kos.sport.routes.user.CreateUserRoute
import de.kos.sport.routes.user.UserRoute
import mu.KotlinLogging
import spark.Spark

object SportsApp {

    //TODO Use json library to build json responses
    //TODO Instead of using "Table.all()" create proper select statements
    //TODO Change json response format to "[ { success: true/false, message: "bla bla", data [ { user: 0 } ] } ]

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

        logger.info { "Spark is listening to port $SPARK_PORT" }

        logger.debug { "Registering routes" }

        //Register spark routes
        Spark.path("/api") {
            Spark.path("/checkpoint") {
                Spark.get("/create/*/*/*/*/*", CheckpointCreateRoute())
                Spark.get("/:id", CheckpointRoute())
                Spark.get("/:id/visit/*", CheckpointVisitRoute())
            }
            Spark.path("/user") {
                Spark.get("/:id", UserRoute())
                Spark.get("/create/*/*/*", CreateUserRoute())
            }
            Spark.path("/session") {
                Spark.get("/create/*/*", SessionCreateRoute())
                Spark.get("/:token/validate", SessionValidateRoute())
                Spark.get("/:token/destroy", SessionDestroyRoute())
            }
            Spark.path("/class") {
                Spark.get("/all/:token", ClassAllRoute())
                Spark.get("/create/*/*", ClassCreateRoute())
                Spark.get("/:id", ClassRoute())
            }
            Spark.path("/student") {
                Spark.get("/:id", StudentRoute())
                Spark.get("/:id/present/*/*/*", StudentPresentRoute())
                Spark.get("/create/*/*/*", StudentCreateRoute())
                Spark.get("/:id/class", StudentClassRoute())
            }
            Spark.path("/stats") {
                Spark.get("/top/student/:count", StatsTopStudentRoute())
                Spark.get("/top/class/:count", StatsTopClassRoute())
                Spark.get("/student/:id", StatsStudentRoute())
                Spark.get("/class/:id", StatsClassRoute())
            }
        }

        logger.info { "SportsApp started" }
    }
}
