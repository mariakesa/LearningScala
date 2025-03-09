package analysis

import scala.util.matching.Regex
import opennlp.tools.sentdetect.{SentenceDetectorME, SentenceModel}
import java.io.InputStream

object FleschKincaidCalculator {

  // Count words in a text
  def countWords(text: String): Int = {
    if (text.isBlank) 0 else text.split("\\s+").length
  }

  // Load OpenNLP Sentence Model
  private val modelStream: InputStream = getClass.getResourceAsStream("/en-sent.bin")
  private val model = new SentenceModel(modelStream)
  private val sentenceDetector = new SentenceDetectorME(model)

  // Count sentences using OpenNLP Sentence Detector
  def countSentences(text: String): Int = {
    sentenceDetector.sentDetect(text).length
  }

  // Count syllables in a word
  def countSyllables(word: String): Int = {
    val vowelGroups: Regex = "[aeiouyAEIOUY]+".r
    val matches = vowelGroups.findAllIn(word).length
    if (matches == 0) 1 else matches
  }

  // Count total syllables in a text
  def countTotalSyllables(text: String): Int = {
    val words = text.split("\\s+")
    words.map(countSyllables).sum
  }

  // Compute Flesch Reading Ease Score (FRES)
  def calculateFRES(text: String): Double = {
    val wordCount = countWords(text)
    val sentenceCount = countSentences(text)
    val syllableCount = countTotalSyllables(text)

    if (wordCount == 0 || sentenceCount == 0) 0.0
    else {
      val fres = 206.835 - (1.015 * wordCount.toDouble / sentenceCount) - (84.6 * syllableCount.toDouble / wordCount)
      math.min(fres, 100) // Cap max score at 100
    }
  }
}
