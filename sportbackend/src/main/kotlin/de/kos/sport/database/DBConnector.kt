package de.kos.sport.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
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
     * Access level for normal admin users
     */
    const val ACCESS_LEVEL_NORMAL = 0
    /**
     * Access level for checkpoint admin users
     */
    const val ACCESS_LEVEL_CHECKPOINT = 1
    /**
     * Access level for global admin users
     */
    const val ACCESS_LEVEL_GLOBAL = 2

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

    /**
     * Creates an entry in the table for visited checkpoints
     */
    fun visitCheckpoint(student: Student, checkpoint: Checkpoint) {
        transaction {
            VisitedCheckpoint.new {
                this.checkpoint = checkpoint
                this.student = student
                this.score = checkpoint.score
            }
            student.score += checkpoint.score
            student.clazz.score += checkpoint.score
        }
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
    fun createClass(name: String): Class {
        return transaction {
            Class.new {
                this.score = 0
                this.name = name
            }
        }
    }

    /**
     * Creates and inserts a user
     * @return the created user entity
     */
    fun createUser(name: String, password: String, type: Int): User {
        return transaction {
            User.new {
                this.name = name
                this.password = password
                this.type = type
            }
        }
    }

    /**
     * @return the session using associated to this token
     */
    fun getSessionFromToken(token: String): Session? {
        return transaction { Session.findById(token) }
    }

    /**
     * Creates a session for the given user
     * @return the generated session token
     */
    fun createSession(user: User): Session {
        return transaction {
            Session.new {
                this.user = user
                this.time = System.currentTimeMillis()
            }
        }
    }

    /**
     * @return true if the token is valid (has user and has no timeout)
     */
    fun validateToken(token: String): Boolean {
        return validateSession(getSessionFromToken(token))
    }

    /**
     * @return true if the session is valid (has user and has no timeout)
     */
    fun validateSession(session: Session?): Boolean {
        return session != null && System.currentTimeMillis() - transaction { session.time } < SESSION_TIMEOUT
    }

    /**
     * @return the user handle by id
     */
    fun getUserById(id: Int): User? {
        return transaction { User.find { Users.id eq id }.firstOrNull() }
    }

    /**
     * @return the user handle by name
     */
    fun getUserByName(name: String): User? {
        return transaction { User.find { Users.name eq name }.firstOrNull() }
    }

    /**
     * @return the student handle by index id
     */
    fun getStudentById(id: Int): Student? {
        return transaction { Student.find { Students.id eq id }.firstOrNull() }
    }

    /**
     * @return the student handle by student id
     */
    fun getStudentByStudentId(studentId: Int): Student? {
        return transaction { Student.find { Students.studentId eq studentId }.firstOrNull() }
    }

    /**
     * @return the checkpoint handle by id
     */
    fun getCheckpointById(id: Int): Checkpoint? {
        return transaction { Checkpoint.find { Checkpoints.id eq id }.firstOrNull() }
    }

    /**
     * @return the checkpoint handle by name
     */
    fun getCheckpointByName(name: String): Checkpoint? {
        return transaction { Checkpoint.find { Checkpoints.name eq name }.firstOrNull() }
    }

    /**
     * @return the class handle by id
     */
    fun getClassById(id: Int): Class? {
        return transaction { Class.find { Classes.id eq id }.firstOrNull() }
    }

    /**
     * @return the class handle by name
     */
    fun getClassByName(name: String): Class? {
        return transaction { Class.find { Classes.name eq name }.firstOrNull() }
    }

    /**
     * @return the visited checkpoint handles
     */
    fun getVisitedCheckpoints(student: Student): Array<VisitedCheckpoint> {
        return transaction {
            VisitedCheckpoint.find { VisitedCheckpoints.student eq student.id.value }.toList().toTypedArray()
        }
    }

    /**
     * @return the visited checkpoint handle
     */
    fun getVisitors(checkpoint: Checkpoint): Array<VisitedCheckpoint> {
        return transaction {
            VisitedCheckpoint.find { VisitedCheckpoints.checkpoint eq checkpoint.id.value }.toList().toTypedArray()
        }
    }

    /**
     * @return the checkpoint handles
     */
    fun getCheckpointsByUser(user: User): Array<Checkpoint> {
        return transaction {
            Checkpoint.find { Checkpoints.user eq user.id.value }.toList().toTypedArray()
        }
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
            SchemaUtils.create(Checkpoints, Users, Students, Classes, VisitedCheckpoints, Sessions)
        }
    }
}

