package util

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class Dictionary {

    private val username = "root"
    private val password = System.getenv()["DB_PASSWORD"]

    var connection: Connection = openNewConnection()

    private fun openNewConnection(): Connection {
        val url = "jdbc:mysql://localhost:3306/entries?useSSL=false"
        return DriverManager.getConnection(url, username, password)
    }

    fun isThisAWord(input: String): Boolean {
        try {
            val stmt = connection.createStatement()
            val resultSet = stmt.executeQuery("select count(*) as result from distinct_words where word = '$input'")
            resultSet.next()
            val count = resultSet.getInt("result")
            return count > 0
        } catch (e: SQLException) {
            connection = openNewConnection()
        }
        return false
    }

}