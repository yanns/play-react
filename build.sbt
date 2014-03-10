name := "play-react"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "react" % "0.9.0",
  "com.typesafe" %% "jse" % "1.0.0-M1",
  "io.apigee.trireme" % "trireme" % "0.7.0"
)

play.Project.playScalaSettings
