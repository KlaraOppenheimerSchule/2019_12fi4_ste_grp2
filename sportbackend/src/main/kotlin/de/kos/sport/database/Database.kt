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
    val score = integer("score").default(0)
}

/**
 * Represents a student row entity of the students table
 */
class Student(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Student>(Students)

    var studentId by Students.studentId
    var clazz by Class referencedOn Students.studentClass
    var score by Students.score

    override fun toString(): String {
        return "{ id: $studentId, score: $score }"
    }
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

    override fun toString(): String {
        return "{ id: score: $score }"
    }
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
