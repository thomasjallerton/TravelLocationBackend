package service

import datastructure.WordCounter
import util.HttpRequest
import java.util.*
import javax.ws.rs.core.Response
import org.jsoup.Jsoup
import util.Dictionary


class FindTopLocationService: FindTopLocationApi {

    val dictionary = Dictionary()

    override fun findTopLocation(url: String): Response {
        return try {
            val html = HttpRequest.getHtml(url)
            val paragraphs = Jsoup.parse(html).select("p")
            val properNouns = extractProperNouns(paragraphs.text())

            val wordCounts = WordCounter(properNouns)
            println(dictionary.isThisAWord(wordCounts.mostFrequent))

            errorResponse("Lols actually ok")
        } catch (e: Exception) {
            errorResponse("Error requesting page")
        }
    }

    private fun extractProperNouns(inputString: String): List<String> {
        val regex = Regex("([A-Z][a-z]+ ?)+")
        val matches = regex.findAll(inputString)
        val result = LinkedList<String>()
        for (match in matches) {
            result.add(match.value)
        }
        return result
    }


    private fun errorResponse(message: String): Response {
        return Response.serverError().entity("{\n\"message\" : \"$message\"\n}").build()
    }
}

fun main(args: Array<String>) {
    FindTopLocationService().findTopLocation("https://medium.com/scribe/happiness-or-some-such-d530d9d29023")
}