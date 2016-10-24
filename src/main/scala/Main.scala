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

  def main(args: Array[String]):Unit = {
    val path = getClass.getResource("/mails")

    println(path)
    if (path.equals(null)) {
      println("there are no mails")
      return
    }

    val file = new File(path.toURI)
    val files = recursiveListFiles(file)
    println(files.length)
    files.map(println)

  }
}
