package de.kos.sport.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlin.random.Random

/**
 * Represents a indexed table of checkpoints
 */
object Checkpoints : IntIdTable() {
    /**
     * The max length for name
     */
    private const val MAX_NAME_SIZE = 255
    /**
     * The max length for location
     */
    private const val MAX_LOCATION_SIZE = 255

    val name = varchar("name", MAX_NAME_SIZE).uniqueIndex()
    val location = varchar("location", MAX_LOCATION_SIZE)
    val score = integer("score")
    val user = reference("user", Users)
}

/**
 * Represents a checkpoint row entity of the checkpoints table
 */
class Checkpoint(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Checkpoint>(Checkpoints)

    var name by Checkpoints.name
    var location by Checkpoints.location
    var score by Checkpoints.score
    var user by User referencedOn Users.id
}

/**
 * Represents a indexed tale of students
 */
object Students : IntIdTable() {
    val studentId = integer("studentId")
    val studentClass = reference("class", Classes)
}

/**
 * Represents a student row entity of the students table
 */
class Student(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Student>(Students)

    var studentId by Students.studentId
    var clazz by Class referencedOn Classes.id
}

/**
 * Represents a indexed table of classes
 */
object Classes : IntIdTable() {
    val score = integer("score")
}

/**
 * Represents a class row entity of the classes table
 */
class Class(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Class>(Classes)

    var score by Classes.score
    val students by Student referrersOn Students.studentClass
}

/**
 * Represents a indexed table of users
 */
object Users : IntIdTable() {
    /**
     * The max length for name
     */
    private const val MAX_NAME_SIZE = 255
    /**
     * The max length for password
     */
    private const val MAX_PASSWORD_SIZE = 255

    val name = varchar("name", MAX_NAME_SIZE).uniqueIndex()
    val password = varchar("password", MAX_PASSWORD_SIZE)
}

/**
 * Represents a user row entity of the users table
 */
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var password by Users.password
}

/**
 * Represents a indexed table of visited checkpoints
 */
object VisitedCheckpoints : IntIdTable() {
    val student = reference("student", Students)
    val checkpoint = reference("checkpoint", Checkpoints)
    val score = integer("score")
}

/**
 * Represents a visited checkpoint row entity of the visited checkpoints table
 */
class VisitedCheckpoint(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<VisitedCheckpoint>(VisitedCheckpoints)

    var student by Student referencedOn Students.id
    var checkpoint by Checkpoint referencedOn Checkpoints.id
    var score by VisitedCheckpoints.score
}

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
