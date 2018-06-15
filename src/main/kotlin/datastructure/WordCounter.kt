package datastructure

class WordCounter() {
    private val wordCounts = HashMap<String, Int>()
    var mostFrequent: String = ""
    private var mostFrequentCount: Int = 0

    constructor(words: List<String>): this() {
        for (word in words) {
            add(word)
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




}