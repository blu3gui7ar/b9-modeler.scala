// Comment to get more information during initialization
//logLevel := Level.Warn

// Sbt plugins
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.7")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.21")

// fat jar
// addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")

addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.6")

// addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")

// addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.1")

addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.9.0")

//addSbtPlugin("com.lihaoyi" % "workbench" % "0.3.0")

// addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.2")

