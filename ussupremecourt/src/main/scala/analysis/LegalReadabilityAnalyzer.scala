package analysis

import org.apache.commons.csv.{CSVFormat, CSVParser}
import scala.io.Source
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import java.io.{BufferedWriter, FileWriter}
import collection.JavaConverters.asScalaIteratorConverter

object LegalReadabilityAnalyzer {
  def main(args: Array[String]): Unit = {
    val filePath = "/home/maria/Documents/LegalData/all_opinions.csv"
    val outputFilePath = "/home/maria/Documents/LegalData/readability_scores.csv"

    // Open CSV file
    val reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)
    val csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())

    val writer = new BufferedWriter(new FileWriter(outputFilePath))
    writer.write("year_filed,FleschReadingEaseScore\n") // Write header

    // Define valid year range
    val validYearRange = 1750 to 2025

    // Process CSV properly (handling quoted fields)
    val readabilityScores = csvParser.getRecords.iterator().asScala.flatMap { record =>
      try {
        val yearFiled = record.get("year_filed").toInt
        if (!validYearRange.contains(yearFiled)) None // Ignore invalid years
        else {
          val text = record.get("text")
          val fres = FleschKincaidCalculator.calculateFRES(text) // Compute FRES
          
          // Write to CSV
          writer.write(s"$yearFiled,$fres\n")
          Some((yearFiled, fres))
        }
      } catch {
        case _: Exception => None // Skip malformed rows
      }
    }.toList

    reader.close()
    writer.close()

    println(s"Readability scores saved to: $outputFilePath")
  }
}
