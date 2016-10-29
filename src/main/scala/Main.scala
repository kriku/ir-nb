import java.io.File

object Main {

  def recursiveListFiles(f: File): Array[File] = {
    if (f.exists) {
      val files = f.listFiles
      files ++ files.filter(_.isDirectory).flatMap(recursiveListFiles)
    } else {
      Array[File]()
    }
  }

  def getResourcePath(path: String):String = {
    val resource = getClass.getResource(path)
    if (resource.equals(null)) {
      println("resource not found")
      System.exit(1)
    }
    resource.getPath
  }

  // Files in one line and don't know how/why do it lazy
  def getDocsFromPath(path: String): Array[Document] = {
    val pathFile = new File(getResourcePath(path))
    val files = recursiveListFiles(pathFile)
    files.filter(_.isFile).map {
      (file) => {
        new Document(file.getName, io.Source.fromFile(file).mkString)
      }
    }
  }

  def conductDictionary(docs: Array[Document]): Map[String, Int] = {
    docs.flatMap(_.tokens).groupBy(identity).mapValues(_.size)
  }

  def main(args: Array[String]):Unit = {
    // to keep track dictionaries separately
    val nsTrainDocs = getDocsFromPath("/mails/nonspam-train")
    val nsTestDocs = getDocsFromPath("/mails/nonspam-test")
    val sTrainDocs = getDocsFromPath("/mails/spam-train")
    val sTestDocs = getDocsFromPath("/mails/spam-test")

    val nsTrainDictionary = conductDictionary(nsTrainDocs)
    val nsTestDictionary = conductDictionary(nsTestDocs)
    val sTrainDictionary = conductDictionary(sTrainDocs)
    val sTestDictionary = conductDictionary(sTestDocs)

    val dictionary = conductDictionary(nsTrainDocs ++ nsTestDocs ++ sTrainDocs ++ sTestDocs)

    val n = (nsTrainDocs ++ sTrainDocs).length
    val tokensTotal = (nsTrainDocs ++ sTrainDocs).flatMap(_.tokens).length
    val tokensInns = nsTrainDocs.flatMap(_.tokens).length
    val tokensIns = sTrainDocs.flatMap(_.tokens).length

    val priorPns = nsTrainDocs.length.toDouble / n
    val priorPs = sTrainDocs.length.toDouble / n

    val condPtermInns = dictionary.map {
      case (term, freq) => {
        (term ->
           priorPns *
           (nsTrainDictionary.getOrElse(term, 0) + 1).toDouble /
           (dictionary.getOrElse(term, 0) + tokensTotal))
      }
    }

    val condPtermIns = dictionary.map {
      case (term, freq) => {
        (term ->
           priorPs *
           (sTrainDictionary.getOrElse(term, 0) + 1).toFloat /
           (dictionary.getOrElse(term, 0) + tokensTotal))
      }
    }

    var count = 0
    for (doc <- nsTestDocs) {
      var scoreNs = Math.log(priorPns)
      doc.dictionary.map {
        case (term, freq) => {
          scoreNs += freq * Math.log(condPtermInns(term))
        }
      }
      var scoreS = Math.log(priorPs)
      doc.dictionary.map {
        case (term, freq) => {
          scoreS += freq * Math.log(condPtermIns(term))
        }
      }
      count += (if (scoreNs > scoreS) 0 else 1)
      if (scoreNs < scoreS) println(s"${doc.id} - false detected as spam")
    }

    println(s"ACCURACY - ${1 - count.toFloat/nsTestDocs.length}")

    count = 0
    for (doc <- sTestDocs) {
      var scoreNs = Math.log(priorPns)
      doc.dictionary.map {
        case (term, freq) => {
          scoreNs += freq * Math.log(condPtermInns(term))
        }
      }
      var scoreS = Math.log(priorPs)
      doc.dictionary.map {
        case (term, freq) => {
          scoreS += freq * Math.log(condPtermIns(term))
        }
      }
      count += (if (scoreNs > scoreS) 0 else 1)
      if (scoreNs > scoreS) println(s"${doc.id} - false detected as non-spam")
    }

    println(s"ACCURACY - ${count.toFloat/nsTestDocs.length}")
  }
}
