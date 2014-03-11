name := "play-react"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "react" % "0.9.0",
  "org.webjars" % "jquery" % "2.1.0-2",
  "com.typesafe" %% "jse" % "1.0.0-M1",
  "io.apigee.trireme" % "trireme" % "0.7.0"
)

play.Project.playScalaSettings

PublicOnFileSystem.settings

// Add a new template type for streaming templates
play.Keys.templatesTypes += ("stream" -> "ui.HtmlStreamFormat")

// Add some useful default imports for streaming templates
play.Keys.templatesImport ++= Vector("_root_.ui.HtmlStream", "_root_.ui.HtmlStream._")
