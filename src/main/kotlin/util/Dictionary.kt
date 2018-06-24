package util

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

private var connection: Connection? = null

class Dictionary {

    private val username = System.getenv()["DB_USERNAME"]
    private val password = System.getenv()["DB_PASSWORD"]
    private val host = System.getenv()["DB_HOST"]
    private val database = System.getenv()["DB_DATABASE"]
    private val port = System.getenv()["DB_PORT"]

    init {
        connection = openNewConnection()
    }

    private fun openNewConnection(): Connection {
        val url = "jdbc:mysql://$host:$port/$database?useSSL=false"
        return DriverManager.getConnection(url, username, password)
    }

    fun isThisAWordOrName(input: String): Boolean {
        try {
            val stmt = connection!!.createStatement()
            val resultSet = stmt.executeQuery("select exists(select 1 from distinct_words where word = '$input' limit 1 union all select 1 from distinct_firstname where firstname = '$input' limit 1)")
            resultSet.next()
            return resultSet.getBoolean(1)
        } catch (e: SQLException) {
            connection = openNewConnection()
        }
        return false
    }

    fun isThisAFirstName(input: String): Boolean {
        try {
            val stmt = connection!!.createStatement()
            val resultSet = stmt.executeQuery("select exists(select 1 from distinct_firstname where firstname = '$input' limit 1)")
            resultSet.next()
            return resultSet.getBoolean(1)
        } catch (e: SQLException) {
            connection = openNewConnection()
        }
        return false
    }

}