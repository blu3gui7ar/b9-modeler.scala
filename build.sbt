lazy val scalaV = "2.12.6"
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// shadow sbt-scalajs' crossProject and CrossType from Scala.js 0.6.x
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

// enablePlugins(DockerPlugin)

scalacOptions ++= Seq("-feature")

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  // pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-slick" % "3.0.0-M3",
    "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0-M3",
    "com.typesafe.slick" %% "slick" % "3.2.0",
    "mysql" % "mysql-connector-java" % "6.0.6",
    "com.lihaoyi" %% "scalatags" % "0.6.5",
    guice,
//    "com.vmunier" %% "scalajs-scripts" % "1.1.0", //not work with js bundler
//    "org.webjars.npm" % "bulma" % "0.4.1",
//    "org.webjars" % "font-awesome" % "4.7.0",
    specs2 % Test,
    filters
  ),
  npmAssets ++= NpmAssets.ofProject(client) { nodeModules => nodeModules / "font-awesome" ** "*" }.value,
  npmAssets ++= NpmAssets.ofProject(client) { nodeModules => nodeModules / "bulma" ** "*" }.value
  // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
  // EclipseKeys.preTasks := Seq(compile in Compile)
).enablePlugins(PlayScala, WebScalaJSBundlerPlugin).
  dependsOn(sharedJvm)

val monocleVersion = "1.5.0"

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  // This is an application with a main method
  scalaJSUseMainModuleInitializer := true,// no need for scalajs-bundler
  useYarn := true,
  mainClass in Compile := Some("b9.ModelerApp"),
  libraryDependencies ++= Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % "1.3.1",
    "com.github.japgolly.scalajs-react" %%% "ext-monocle" % "1.3.1",
    "com.github.julien-truffaut" %%%  "monocle-core"  % monocleVersion,
    "com.github.julien-truffaut" %%%  "monocle-macro" % monocleVersion,
//      "com.github.julien-truffaut" %%% "monocle-law"   % monocleVersion % Test,
    "io.monix" %%% "monix" % "3.0.0-RC2",
    "com.github.japgolly.scalacss" %%% "ext-react" % "0.5.5",
    "com.payalabs" %%% "scalajs-react-bridge" % "0.7.0",
//    "com.payalabs" %%% "scalajs-react-mdl" % "0.2.0-SNAPSHOT",
    "org.scala-js" %%% "scalajs-dom" % "0.9.6"
  ),
  webpackBundlingMode := BundlingMode.LibraryOnly(),
  npmDependencies in Compile ++= Seq(
    "react" -> "16.5.1",
    "react-dom" -> "16.5.1",
    "d3-hierarchy" -> "1.1.5",
    "d3-shape" -> "1.2.0",
    "font-awesome" -> "4.7.0",
    "@material-ui/core" -> "3.9.1",
    "bulma" -> "0.4.1"
  ), npmDevDependencies in Compile += "expose-loader" -> "0.7.1"
).enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

lazy val shared = (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("shared")).settings(
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      "org.scalaz" %%% "scalaz-core" % "7.2.17",
//      "com.lihaoyi" %%% "upickle" % "0.4.4",
      "com.typesafe.play" %%% "play-json" % "2.6.7",
      "com.lihaoyi" %%% "autowire" % "0.2.6",
      "com.lihaoyi" %%% "fastparse" % "2.1.0",
      "org.scalactic" %%% "scalactic" % "3.0.1",
      "org.scalatest" %%% "scalatest" % "3.0.1" % Test,
//      "com.github.kenbot" %% "goggles-dsl" % "1.0",
//      "com.github.kenbot" %% "goggles-macros" % "1.0"
    )
  ).jsConfigure(_ enablePlugins ScalaJSWeb)

// loads the server project at sbt startup
onLoad in Global ~= (_ andThen ("project server" :: _))
