import play.twirl.sbt.Import.TwirlKeys

name := "play-react"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.webjars" % "react" % "0.11.2",
  "org.webjars" % "jquery" % "2.1.1",
  "com.typesafe" %% "jse" % "1.0.0",
  "io.apigee.trireme" % "trireme" % "0.8.2"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

PublicOnFileSystem.settings

// Add a new template type for streaming templates
TwirlKeys.templateFormats += ("stream" -> "ui.HtmlStreamFormat")

// Add some useful default imports for streaming templates
TwirlKeys.templateImports ++= Vector("_root_.ui.HtmlStream", "_root_.ui.HtmlStream._")
