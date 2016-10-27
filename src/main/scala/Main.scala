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

  // I don't fully understand how I got it
  // foldLeft => foldLeft - is this ok?
  def conductDictionary(docs: Array[Document]): Map[String, Int] = {
    docs.flatMap(_.words).groupBy(identity).mapValues(_.size)
    // docs.foldLeft(Map.empty[String, Int]) {
    //   (map, doc) => doc.dictionary.foldLeft(map) {
    //     case (map, (w, v)) => map + (w -> (map.getOrElse(w, 0) + v))
    //   }
    // }
  }

  def main(args: Array[String]):Unit = {
    // also grab Readme (wich shouldn't)... but not deside yet how conduct whole
    // val mails = getDocsFromPath("/mails")
    // val dictionary = conductDictionary(mails)
    // println(dictionary.toSeq.sortWith(_._2 > _._2).take(10))

    // to keep track dictionaries separately
    val nsTrainDocs = getDocsFromPath("/mails/spam-train")
    val nsTestDocs = getDocsFromPath("/mails/nonspam-test")
    val sTrainDocs = getDocsFromPath("/mails/nonspam-train")
    val sTestDocs = getDocsFromPath("/mails/spam-test")

    val nsTrainDictionary = conductDictionary(nsTrainDocs)
    val nsTestDictionary = conductDictionary(nsTrainDocs)
    val sTrainDictionary = conductDictionary(nsTrainDocs)
    val sTestDictionary = conductDictionary(nsTrainDocs)

    val dictionary = conductDictionary(nsTrainDocs ++ nsTestDocs ++ sTrainDocs ++ sTestDocs)

    println(dictionary.toSeq.sortWith(_._2 > _._2).take(10))
    println(nsTrainDictionary.toSeq.sortWith(_._2 > _._2).take(10))
  }
}
