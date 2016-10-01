# sbt-boilerplate
Just boilerplate for scala projects with sbt and ensime

For ensime sbt plugin, need generate `.ensime` by:
```bash
sbt
> ensimeConfig
> ensimeConfigProject
```

Also should be setup `.sbt/../plugins/plugins.sbt`

```scala
if (sys.props("java.version").startsWith("1.6"))
  addSbtPlugin("org.ensime" % "sbt-ensime" % "1.0.0")
else
  addSbtPlugin("org.ensime" % "sbt-ensime" % "1.9.1")
```
