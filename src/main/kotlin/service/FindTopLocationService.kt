package service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import datastructure.WordCounter
import org.jsoup.Jsoup
import util.Dictionary
import util.HttpRequest
import java.util.*
import javax.ws.rs.core.Response


class FindTopLocationService: FindTopLocationApi {

    val dictionary = Dictionary()

    override fun findTopLocation(url: String, numberOfResultsParam: Int): Response {
        return try {
            val html = HttpRequest.getHtml(url)
            val paragraphs = Jsoup.parse(html).select("p")
            val properNouns = extractProperNouns(paragraphs.text())

            val wordCounts = WordCounter.getOrderedWords(properNouns)
            val result = Result()
            var numberOfResults = numberOfResultsParam
            for (wordCount in wordCounts) {
                if (!dictionary.isThisAWord(wordCount.word)) {
                    result.words.add(wordCount)
                    numberOfResults--
                    if (numberOfResults == 0) break
                }
            }
            if (result.words.isEmpty()) {
                errorResponse("No location found")
            } else {
                val stringResult = jacksonObjectMapper().writeValueAsString(result)
                println(stringResult)
                Response.ok().entity(stringResult).build()
            }
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

    private class Result(val words: MutableList<WordCounter.WordCount> = LinkedList())
}

fun main(args: Array<String>) {
    FindTopLocationService().findTopLocation("https://medium.com/adventuretaco/death-valley-day-2-colors-everywhere-d44480a9b096?source=topic_page---8------1------------------1", 3)
}