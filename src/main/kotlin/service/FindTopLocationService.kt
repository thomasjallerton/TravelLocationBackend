package service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import datastructure.WordCounter
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import util.Dictionary
import util.HttpRequest
import java.util.*
import javax.ws.rs.core.Response

val commonWords: Set<String> = setOf("In", "And", "He", "She", "It", "Those" ,"The", "No", "Like", "But", "Because", "We",
        "You", "Which", "This", "If", "That", "When", "Not", "No", "Well", "What", "His", "Hers", "From")

class FindTopLocationService: FindTopLocationApi {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val dictionary = Dictionary()

    override fun findTopLocation(url: String, numberOfResultsParam: Int): Response {
        return try {
            logger.debug("About to execute http request")
            val html = HttpRequest.getHtml(url)
            val paragraphs = Jsoup.parse(html).select("p")
            val properNouns = extractProperNouns(paragraphs.text())

            logger.debug("About to sort words")

            val wordCounts = WordCounter.getOrderedWords(properNouns)
            val result = Result()
            var numberOfResults = numberOfResultsParam
            for (wordCount in wordCounts) {
                if (!commonWords.contains(wordCount.word)) {
                    logger.debug("Executing dictionary lookup")
                    if (!dictionary.isThisAWord(wordCount.word)) {
                        result.words.add(wordCount)
                        numberOfResults--
                        if (numberOfResults == 0) break
                    }
                }
            }
            if (result.words.isEmpty()) {
                errorResponse("No location found")
            } else {
                val stringResult = jacksonObjectMapper().writeValueAsString(result)
                Response.ok().entity(stringResult).build()
            }
        } catch (e: Exception) {
            logger.debug(e.message, e)
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

    private class Result(val words: MutableList<WordCounter.WordCount> = LinkedList())
}

fun main(args: Array<String>) {
    FindTopLocationService().findTopLocation("https://medium.com/@DavidBindel/a-week-in-shanghai-8af0e95ea230", 3)
}