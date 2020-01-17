package de.kos.sport

import de.kos.sport.database.DBConnector
import de.kos.sport.routes.`class`.ClassAllRoute
import de.kos.sport.routes.`class`.ClassCreateRoute
import de.kos.sport.routes.`class`.ClassDeleteRoute
import de.kos.sport.routes.`class`.ClassRoute
import de.kos.sport.routes.checkpoint.*
import de.kos.sport.routes.session.SessionCreateRoute
import de.kos.sport.routes.session.SessionDestroyRoute
import de.kos.sport.routes.session.SessionValidateRoute
import de.kos.sport.routes.stats.`class`.StatsClassRoute
import de.kos.sport.routes.stats.student.StatsStudentRoute
import de.kos.sport.routes.stats.top.`class`.StatsTopClassRoute
import de.kos.sport.routes.stats.top.student.StatsTopStudentRoute
import de.kos.sport.routes.student.*
import de.kos.sport.routes.user.*
import mu.KotlinLogging
import spark.Spark

object SportsApp {

    //TODO Use json library to build json responses
    //TODO Change json response format to "[ { success: true/false, message: "bla bla", data [ { user: 0 } ] } ]

    val logger = KotlinLogging.logger("SportsApp")

    private const val SPARK_PORT = 8181

    val mountain = arrayOf(
        "There is no way into the mountain",
        "There is no way into the mountain",
        "There is no way",
        "There is no fucking way into the mountain",
        "NO",
        "There is no way into the mountain",
        "AHHHHHHHHHH"
    )
    var mountainIndex = 0

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

        Spark.notFound { req, res ->
            try {
                val mnt = mountain[mountainIndex]
                mountainIndex++
                mountainIndex %= mountain.size

                "<h1 style=\"display: inline-block; margin-right: 10px;\">404</h1>" +
                        " <h2 style=\"display: inline-block;\">$mnt</h2><br>" +
                        "<iframe src=\"https://giphy.com/embed/14dfyATDmRCO6A\" width=\"480\" height=\"321\"" +
                        " frameBorder=\"0\" class=\"giphy-embed\" allowFullScreen></iframe><p>" +
                        "<a href=\"https://giphy.com/gifs/balin-14dfyATDmRCO6A\"></a></p>"
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        Spark.internalServerError { req, res -> "<h1 style=\"display: inline-block; margin-right: 10px;\">500</h1>" +
                " <h2 style=\"display: inline-block;\">Cheese Knife</h2>" }

        //Register spark routes
        Spark.path("/api") {
            Spark.path("/checkpoint") {
                Spark.get("/all/:token", CheckpointAllRoute())
                Spark.get("/create/*/*/*/*/*", CheckpointCreateRoute())
                Spark.get("/:id/update/*/*/*/*/*", CheckpointUpdateRoute())
                Spark.get("/:id/delete/*", CheckpointDeleteRoute())
                Spark.get("/:id", CheckpointRoute())
                Spark.get("/:id/visit/*/*", CheckpointVisitRoute())
                Spark.get("/:id/visits/*", CheckpointVisitsRoute())
            }
            Spark.path("/user") {
                Spark.get("/:id", UserRoute())
                Spark.get("/create/*/*/*", UserCreateRoute())
                Spark.get("/:id/delete/*", UserDeleteRoute())
                Spark.get("/:id/update/*/*/*", UserUpdateRoute())
                Spark.get("/:id/checkpoints/*", UserCheckpointsRoute())
            }
            Spark.path("/session") {
                Spark.get("/create/*/*", SessionCreateRoute())
                Spark.get("/:token/validate", SessionValidateRoute())
                Spark.get("/:token/destroy", SessionDestroyRoute())
            }
            Spark.path("/class") {
                Spark.get("/all/:token", ClassAllRoute())
                Spark.get("/create/*/*", ClassCreateRoute())
                Spark.get("/:id/delete/*", ClassDeleteRoute())
                Spark.get("/:id", ClassRoute())
            }
            Spark.path("/student") {
                Spark.get("/:id", StudentRoute())
                Spark.get("/:id/present/*/*", StudentPresentRoute())
                Spark.get("/create/*/*/*", StudentCreateRoute())
                Spark.get("/:id/class", StudentClassRoute())
                Spark.get("/:id/visits", StudentVisitsRoute())
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
