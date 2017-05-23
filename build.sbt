lazy val scalaV = "2.12.2"
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js


enablePlugins(DockerPlugin)


lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  // pipelineStages := Seq(digest, gzip),
  // triggers sbtalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-slick" % "3.0.0-M3",
    "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0-M3",
    "com.typesafe.slick" %% "slick" % "3.2.0",
    "mysql" % "mysql-connector-java" % "6.0.6",
    "com.lihaoyi" %%% "scalatags" % "0.6.5",
//    "com.vmunier" %% "scalajs-scripts" % "1.1.0", //not work with js bundler
//    "org.webjars.npm" % "bulma" % "0.4.1",
//    "org.webjars" % "font-awesome" % "4.7.0",
    specs2 % Test,
    filters
  ),
  npmAssets ++= NpmAssets.ofProject(client) { nodeModules => (nodeModules / "font-awesome").*** }.value,
  npmAssets ++= NpmAssets.ofProject(client) { nodeModules => (nodeModules / "bulma").*** }.value
  // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
  // EclipseKeys.preTasks := Seq(compile in Compile)
).enablePlugins(PlayScala, WebScalaJSBundlerPlugin).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  // This is an application with a main method
  scalaJSUseMainModuleInitializer := true,
  mainClass in Compile := Some("b9.ModelerApp"),
  // persistLauncher := true,
  // persistLauncher in Test := false,
  libraryDependencies ++= Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % "1.0.0",
//    "com.github.japgolly.scalajs-react" %%% "ext-monocle" % "1.0.0",
//    "com.github.julien-truffaut" %%%  "monocle-core"  % "1.4.0",
//    "com.github.julien-truffaut" %%%  "monocle-macro" % "1.4.0",
    "io.suzaku" %%% "diode" % "1.1.2",
    "io.suzaku" %%% "diode-react" % "1.1.2",
    "com.github.japgolly.scalacss" %%% "ext-react" % "0.5.3",
    "org.scala-js" %%% "scalajs-dom" % "0.9.1"
  ),
  npmDependencies in Compile ++= Seq(
    "react" -> "15.5.4",
    "react-dom" -> "15.5.4",
    "font-awesome" -> "4.7.0",
    "bulma" -> "0.4.1"
  ), npmDevDependencies in Compile += "expose-loader" -> "0.7.1"
).enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb).
  dependsOn(sharedJs)


lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "0.4.4",
      "com.lihaoyi" %%% "autowire" % "0.2.6",
      "com.lihaoyi" %%% "fastparse" % "0.4.2",
      "org.scalactic" %%% "scalactic" % "3.0.1",
      "org.scalatest" %%% "scalatest" % "3.0.1" % Test
    )
  ).jsConfigure(_ enablePlugins ScalaJSWeb)

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value
