package datastructure

import java.util.*

class WordCounter() {
    private val wordCounts = HashMap<String, Int>()
    var mostFrequent: String = ""
    private var mostFrequentCount: Int = 0

    constructor(words: List<String>): this() {
        for (word in words) {
            add(word.trim())
        }
    }

    fun add(element: String) {
        var currentCount = wordCounts.getOrDefault(element, 0)
        currentCount++
        wordCounts[element] = currentCount

        if (mostFrequentCount < currentCount) {
            mostFrequentCount = currentCount
            mostFrequent = element
        }
    }

    fun getOrderedWords(): List<WordCount> {
        val listToOrder = LinkedList<WordCount>()
        for (entry in wordCounts) {
            listToOrder.add(WordCount(entry.key, entry.value))
        }

        listToOrder.sort()
        return listToOrder
    }

    inner class WordCount(val word: String, val count: Int): Comparable<WordCount> {

        override fun compareTo(other: WordCount): Int {
            return -this.count.compareTo(other.count)
        }
    }

    companion object {
        fun getOrderedWords(words: List<String>): List<WordCount> {
            val wordCounter = WordCounter(words)
            return wordCounter.getOrderedWords()
        }
    }
}