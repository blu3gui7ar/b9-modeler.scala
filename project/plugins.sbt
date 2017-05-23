// Comment to get more information during initialization
//logLevel := Level.Warn

// Sbt plugins
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0-M5")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.16")

// fat jar
// addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")

addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.4")

// addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")

// addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.1")

addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.6.0")

//addSbtPlugin("com.lihaoyi" % "workbench" % "0.3.0")

// addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")

