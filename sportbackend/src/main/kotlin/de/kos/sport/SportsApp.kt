package de.kos.sport

import de.kos.sport.database.DBConnector
import de.kos.sport.database.Student
import de.kos.sport.database.Students
import mu.KotlinLogging
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import spark.Spark
import de.kos.sport.database.*
import de.kos.sport.routes.`class`.ClassCreateRoute
import de.kos.sport.routes.`class`.ClassRoute
import de.kos.sport.routes.login.LoginRoute
import de.kos.sport.routes.stats.`class`.StatsClassRoute
import de.kos.sport.routes.stats.student.StatsStudentRoute
import de.kos.sport.routes.stats.top.`class`.StatsTopClassRoute
import de.kos.sport.routes.stats.top.student.StatsTopStudentRoute
import de.kos.sport.routes.student.StudentCreateRoute
import de.kos.sport.routes.user.CreateUserRoute
import de.kos.sport.routes.user.UserRoute
import de.kos.sport.routes.user.ValidateRoute
import java.sql.SQLException

object SportsApp {

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

        //Register spark routes
        Spark.path("/api") {
            Spark.path("/user") {
                Spark.get("/:id", UserRoute())
                Spark.get("/create/*/*/*", CreateUserRoute())
                Spark.get("/validate/:token", ValidateRoute())
            }
            Spark.path("/login") {
                Spark.get("/*/*", LoginRoute())
            }
            Spark.path("/class") {
                Spark.get("/create/*/*", ClassCreateRoute())
                Spark.get("/:id", ClassRoute())
            }
            Spark.path("/student") {
                Spark.get("/create/*/*/*", StudentCreateRoute())
            }
            Spark.path("/stats") {
                Spark.get("/top/student/:count", StatsTopStudentRoute())
                Spark.get("/top/class/:count", StatsTopClassRoute())
                Spark.get("/student/:id", StatsStudentRoute())
                Spark.get("/class/:id", StatsClassRoute())
            }
        }
    }
}
