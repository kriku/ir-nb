class Document (val id: String, val text: String) {
  val tokens = text.split("\\s+")
  val dictionary = tokens.groupBy(identity).mapValues(_.size)

  override def toString = {
    s"$id : words - ${tokens.length}, distinct - ${dictionary.size}"
  }
}
