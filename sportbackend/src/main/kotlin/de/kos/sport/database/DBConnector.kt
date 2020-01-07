package de.kos.sport.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * DBConnector initializes and provides the connection
 * to the database and exposes basic database functions
 * for data modifications and parsing
 */
object DBConnector {
    /**
     * The student id minimum
     */
    private const val MIN_STUDENT_ID = 100000
    /**
     * The student id maximum
     */
    private const val MAX_STUDENT_ID = 999999

    /**
     * The session timeout in milliseconds
     * Currently: 3 hours
     */
    private const val SESSION_TIMEOUT = 10800000L

    /**
     * Validates the given student id
     * @return true if the student id is valid (already in use)
     */
    fun validateStudentId(studentId: Int): Boolean {
        return transaction { !Student.find { Students.studentId eq studentId }.empty() }
    }

    /**
     * Generates a random student id, that is not already in use
     * @return the random student id
     */
    fun getRandomStudentId(): Int {
        val studentId = Random.nextInt(MIN_STUDENT_ID, MAX_STUDENT_ID)

        if (validateStudentId(studentId)) {
            return getRandomStudentId()
        }

        return studentId
    }

    fun visitCheckpoint(student: Student, checkpoint: Checkpoint) {
    }

    /**
     * Creates and inserts a checkpoint
     * @return the created checkpoint entity
     */
    fun createCheckpoint(name: String, location: String, score: Int, user: User): Checkpoint {
        return transaction {
            Checkpoint.new {
                this.name = name
                this.location = location
                this.score = score
                this.user = user
            }
        }
    }

    /**
     * Creates and inserts a student
     * @return the created student entity
     */
    fun createStudent(clazz: Class): Student {
        return transaction {
            Student.new {
                this.studentId = getRandomStudentId()
                this.clazz = clazz
            }
        }
    }

    /**
     * Creates and inserts a class
     * @return the created class entity
     */
    fun createClass(): Class {
        return transaction {
            Class.new {
                this.score = 0
            }
        }
    }

    /**
     * Creates and inserts a user
     * @return the created user entity
     */
    fun createUser(name: String, password: String): User {
        return transaction {
            User.new {
                this.name = name
                this.password = password
            }
        }
    }

    /**
     * @return the user using this token
     */
    fun getUserFromToken(token: String): User? {
        return transaction { User.all().find { it.token == token } }
    }

    /**
     * Refreshes the token of a user
     * also resets the last login time
     */
    fun refreshToken(user: User, token: String) {
        transaction {
            user.token = token
            user.lastLogin = System.currentTimeMillis()
        }
    }

    /**
     * @return true if the token is valid (has user and has no timeout)
     */
    fun validateToken(token: String): Boolean {
        val user = getUserFromToken(token)

        return user != null && System.currentTimeMillis() - transaction { user.lastLogin } < SESSION_TIMEOUT
    }

    /**
     * Initializes and configures the database driver and connection pool
     */
    fun init() {
        java.lang.Class.forName("org.postgresql.Driver")

        val config = HikariConfig()
        config.jdbcUrl = "jdbc:postgresql://exposed.flaflo.xyz:5432/sportsapp"
        config.username = "postgres"
        config.password = File(System.getProperty("user.dir") + "/database.pw").readText()
        config.driverClassName = "org.postgresql.Driver"

        val hikariDataSource = HikariDataSource(config)

        Database.connect(hikariDataSource)
        transaction {
            SchemaUtils.create(Checkpoints, Users, Students, Classes, VisitedCheckpoints)
        }
    }
}

