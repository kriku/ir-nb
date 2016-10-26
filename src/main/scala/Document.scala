class Document (val id: String, val text: String) {
  val words = text.split("\\s+")
  val dictionary = words.groupBy(identity).mapValues(_.size)

  override def toString = {
    s"$id : words - ${words.length}, distinct - ${dictionary.size}"
  }
}
