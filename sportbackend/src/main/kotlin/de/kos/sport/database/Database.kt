package de.kos.sport.database

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

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
    var user by User referencedOn Checkpoints.user

    override fun toString(): String {
        return transaction {
            "{ \"id\": $id, \"name\": \"$name\", \"location\": \"$location\", \"score\": $score, \"user\": ${user.id} }"
        }
    }
}

/**
 * Represents a indexed tale of students
 */
object Students : IntIdTable() {
    val studentId = integer("studentId").primaryKey()
    val studentClass = reference("class", Classes)
    val score = integer("score").default(0)
    val present = bool("present").default(true)
}

/**
 * Represents a student row entity of the students table
 */
class Student(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Student>(Students)

    var studentId by Students.studentId
    var clazz by Class referencedOn Students.studentClass
    var score by Students.score

    var present by Students.present

    override fun toString(): String {
        return "{ \"id\": $studentId, \"score\": $score }"
    }
}

/**
 * Represents a indexed table of classes
 */
object Classes : IntIdTable() {
    /**
     * The max length for name
     */
    private const val MAX_NAME_SIZE = 255

    val score = integer("score")
    val name = varchar("name", MAX_NAME_SIZE).uniqueIndex()
}

/**
 * Represents a class row entity of the classes table
 */
class Class(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Class>(Classes)

    var score by Classes.score
    val students by Student referrersOn Students.studentClass
    var name by Classes.name

    override fun toString(): String {
        val sb = StringBuilder()
        transaction {
            students.forEachIndexed { id, it ->
                sb.append(it)

                if (id < students.count() - 1) {
                    sb.append(", ")
                }
            }
        }

        return "{ \"id\": ${this.id}, \"name\": \"$name\", \"score\": $score, \"students\": [$sb] }"
    }

    fun toLightweightString(): String {
        val studentCount = transaction { students.count() }

        return "{ \"id\": ${this.id}, \"name\": \"$name\", \"score\": $score, \"students\": $studentCount }"
    }
}

/**
 * Represents a indexed table of users
 */
object Users : IntIdTable() {
    /**
     * The max length for name
     */
    private const val MAX_NAME_LENGTH = 255
    /**
     * The max length for password
     */
    private const val MAX_PASSWORD_LENGTH = 255

    val name = varchar("name", MAX_NAME_LENGTH).uniqueIndex()
    val password = varchar("password", MAX_PASSWORD_LENGTH)

    val type = integer("type")
}

/**
 * Represents a user row entity of the users table
 */
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var password by Users.password

    var type by Users.type

    override fun toString(): String {
        return "{ \"id\": ${this.id}, \"name\": \"$name\", \"type\": $type }"
    }
}

/**
 * Represents a visited checkpoint row entity of the visited checkpoints table
 */
class VisitedCheckpoint(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<VisitedCheckpoint>(VisitedCheckpoints)

    var student by Student referencedOn VisitedCheckpoints.student
    var checkpoint by Checkpoint referencedOn VisitedCheckpoints.checkpoint
    var score by VisitedCheckpoints.score

    override fun toString(): String {
        return transaction { "{ \"checkpoint\": ${checkpoint.id}, \"student\": ${student.id}, \"score\": $score" }
    }
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
 * Represents a indexed table of sessions
 */
object Sessions : IdTable<String>() {
    /**
     * The max length for tokens
     */
    private const val MAX_TOKEN_LENGTH = 32

    override val id =
        varchar("token", MAX_TOKEN_LENGTH).primaryKey()
            .clientDefault() { UUID.randomUUID().toString().replace("-", "") }
            .entityId()

    val user = reference("user", Users)
    val time = long("time")
}

/**
 * Represents a session row entity of the session table
 */
class Session(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Session>(Sessions)

    var user by User referencedOn Sessions.user
    var time by Sessions.time
}
