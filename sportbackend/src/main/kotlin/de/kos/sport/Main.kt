package de.kos.sport

import de.kos.sport.database.DBConnector

/**
 * The programs entrypoint
 */
fun main() {
    DBConnector.init()
    SportsApp.start()
}
