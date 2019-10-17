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

object Checkpoints : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex()
    val location = varchar("location", 255)
    val score = integer("score")
    val user = reference("user", Users)
}

class Checkpoint(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Checkpoint>(Checkpoints)

    var name by Checkpoints.name
    var location by Checkpoints.location
    var score by Checkpoints.score
    var user by User referencedOn Users.id
}

object Students : IntIdTable() {
    val studentId = integer("studentId")
    val studentClass = reference("class", Classes)
}

class Student(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Student>(Students)

    var studentId by Students.studentId
    var clazz by Class referencedOn Classes.id
}

object Classes : IntIdTable() {
    val score = integer("score")
}

class Class(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Class>(Classes)

    var score by Classes.score
    val students by Student referrersOn Students.studentClass
}

object Users : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex()
    val password = varchar("password", 255)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var password by Users.password
}

object VisitedCheckpoints : IntIdTable() {
    val student = reference("student", Students)
    val checkpoint = reference("checkpoint", Checkpoints)
    val score = integer("score")
}

class VisitedCheckpoint(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<VisitedCheckpoint>(VisitedCheckpoints)

    var student by Student referencedOn Students.id
    var checkpoint by Checkpoint referencedOn Checkpoints.id
    var score by VisitedCheckpoints.score
}

object DBConnector {

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
        val studentId = Random.nextInt(100000, 999999)

        if (validateStudentId(studentId)) {
            return getRandomStudentId()
        }

        return studentId
    }

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

    fun createStudent(clazz: Class): Student {
        return transaction {
            Student.new {
                this.studentId = getRandomStudentId()
                this.clazz = clazz
            }
        }
    }

    fun createClass(): Class {
        return transaction {
            Class.new {
                this.score = 0
            }
        }
    }

    fun createUser(name: String, password: String): User {
        return transaction {
            User.new {
                this.name = name
                this.password = password
            }
        }
    }

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