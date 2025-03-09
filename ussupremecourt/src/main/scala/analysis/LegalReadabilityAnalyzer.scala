package analysis

import scala.io.Source

object LegalReadabilityAnalyzer {
  def main(args: Array[String]): Unit = {
    val filePath = "/home/maria/Documents/LegalData/all_opinions.csv"
    val source = Source.fromFile(filePath)
    val lines = source.getLines()

    // Read header row and determine column indices
    val headers = lines.next().split(",").map(_.trim)
    val yearIndex = headers.indexOf("year_filed")
    val textIndex = headers.indexOf("text")

    // Ensure columns exist
    if (yearIndex == -1 || textIndex == -1) {
      println("Error: CSV does not contain 'year_filed' or 'text' columns.")
      source.close()
      return
    }

    // Process each line safely
    val readabilityScores = lines.flatMap { line =>
      val columns = line.split(",").map(_.trim)
      if (columns.length > math.max(yearIndex, textIndex)) {
        try {
          val yearFiled = columns(yearIndex).toInt
          val text = columns(textIndex)
          val fkgl = FleschKincaidCalculator.calculateFKGL(text)
          Some(yearFiled, fkgl)
        } catch {
          case _: Exception => None // Ignore malformed rows
        }
      } else None
    }.toList

    source.close()

    // Print first few results
    readabilityScores.take(5).foreach { case (year, fkgl) =>
      println(s"Year: $year, Flesch-Kincaid Grade Level: $fkgl")
    }
  }
}
