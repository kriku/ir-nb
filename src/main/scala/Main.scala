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

  def getDocsFromPath(path: String): Array[Document] = {
    val pathFile = new File(getResourcePath(path))
    val files = recursiveListFiles(pathFile)
    files.filter(_.isFile)
      .map(file => {
             new Document(file.getName,
                          io.Source.fromFile(file).mkString)
           })
  }

  def main(args: Array[String]):Unit = {
    val nonspamTrainDocs = getDocsFromPath("/mails/spam-train")
    val nonspamTestDocs = getDocsFromPath("/mails/nonspam-test")
    val spamTrainDocs = getDocsFromPath("/mails/nonspam-train")
    val spamTestDocs = getDocsFromPath("/mails/spam-test")

    nonspamTestDocs.map(println)
  }
}
