package de.kos.sport

import com.google.gson.GsonBuilder
import de.kos.sport.database.DBConnector
import de.kos.sport.database.Student
import de.kos.sport.database.Students
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * The programs entrypoint
 */
fun main() {
    DBConnector.init()


    //SportsApp.start()
}
