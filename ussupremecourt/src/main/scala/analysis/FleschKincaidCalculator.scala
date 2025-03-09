package analysis

import scala.util.matching.Regex

object FleschKincaidCalculator { 
  def countWords(text: String): Int = {
    if (text.isBlank) 0 else text.split("\\s+").length
  }

  def countSentences(text: String): Int = {
    val sentenceEndings = "[.!?]+"
    text.split(sentenceEndings).length
  }

  def countSyllables(word: String): Int = {
    val vowelGroups: Regex = "[aeiouyAEIOUY]+".r
    val matches = vowelGroups.findAllIn(word).length
    if (matches == 0) 1 else matches
  }

  def countTotalSyllables(text: String): Int = {
    val words = text.split("\\s+")
    words.map(countSyllables).sum
  }

  def calculateFKGL(text: String): Double = {
    val wordCount = countWords(text)
    val sentenceCount = countSentences(text)
    val syllableCount = countTotalSyllables(text)

    if (wordCount == 0 || sentenceCount == 0) 0.0
    else {
      val asl = wordCount.toDouble / sentenceCount
      val asw = syllableCount.toDouble / wordCount
      0.39 * asl + 11.8 * asw - 15.59
    }
  }
}
