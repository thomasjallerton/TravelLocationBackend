package util

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import kotlin.collections.HashSet

private var connection: Connection? = null

class Dictionary {

    private val username = System.getenv()["DB_USERNAME"]
    private val password = System.getenv()["DB_PASSWORD"]
    private val host = System.getenv()["DB_HOST"]
    private val database = System.getenv()["DB_DATABASE"]
    private val port = System.getenv()["DB_PORT"]

    private val words = LinkedList<String>()
    private val names = LinkedList<String>()

    private val wordMapping = HashMap<String, String>()

    init {
        connection = openNewConnection()
    }

    fun addWord(word: String) {
        words.add(word)
    }

    fun addWord(word: String, mapping: String) {
        words.add(word)
        wordMapping[word] = mapping
    }

    fun addName(name: String, mapping: String) {
        names.add(name)
        wordMapping[name] = mapping
    }

    fun addName(name: String) {
        names.add(name)
    }

    private fun openNewConnection(): Connection {
        val url = "jdbc:mysql://$host:$port/$database?useSSL=false"
        return DriverManager.getConnection(url, username, password)
    }

    fun findWordsAndNames(): Set<String> {
        val result = HashSet<String>()
        try {
            val stmt = connection!!.createStatement()
            val stringNames = convertToString(names)
            val stringWords = convertToString(words)
            val query = "select firstname from distinct_firstname where firstname in ($stringNames) union all select word from distinct_words where word in ($stringWords);"
            val resultSet = stmt.executeQuery(query)
            while (resultSet.next()) {
                val wordOrName = resultSet.getString(1)
                result.add(wordMapping.getOrDefault(wordOrName, wordOrName))
            }
        } catch (e: SQLException) {
            connection = openNewConnection()
        }
        return result
    }

    private fun convertToString(list: List<String>): String {
        var result = ""
        for (item in list) {
            result += "'$item',"
        }
        return result.dropLast(1)
    }
}