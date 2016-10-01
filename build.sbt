val scalactic = "org.scalactic" %% "scalactic" % "3.0.0"
val scalatest = "org.scalatest" %% "scalatest" % "3.0.0" % "test"

lazy val root = (project in file(".")).settings(
  libraryDependencies += scalactic,
  libraryDependencies += scalatest
)
