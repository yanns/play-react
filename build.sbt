name := "play-react"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "react" % "0.8.0"
)

play.Project.playScalaSettings
