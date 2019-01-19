name := "play-example-login"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)     
libraryDependencies += "com.googlecode.json-simple" % "json-simple" % "1.1.1"

play.Project.playJavaSettings
