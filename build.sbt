import sbtcrossproject.{crossProject, CrossType}

val bakerVersion = "3.0.0-SNAPSHOT"
val akkaVersion = "2.5.21"

val bakerRuntime =  "com.ing.baker" %% "baker-runtime"  % bakerVersion
val bakerCompiler = "com.ing.baker" %% "baker-compiler" % bakerVersion

val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.1.1"
val scalaJsScripts = "com.vmunier" %% "scalajs-scripts" % "1.1.2"
val liftJson =                  "net.liftweb"                %% "lift-json"                          % "3.3.0"
val kryo =                      "com.esotericsoftware"       % "kryo"                                % "4.0.0"
val kryoSerializers =           "de.javakaffee"              %  "kryo-serializers"                   % "0.41"
val graphVizJava = "guru.nidi" % "graphviz-java" % "0.8.3"

lazy val server = (project in file("server")).settings(commonSettings).settings(
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    "com.lihaoyi" %% "scalatags" % "0.6.7",
    akkaHttp,
    akkaStream,
    scalaJsScripts,
    bakerRuntime,
    bakerCompiler,
    liftJson,
    graphVizJava
  ),
  WebKeys.packagePrefix in Assets := "public/",
  managedClasspath in Runtime += (packageBin in Assets).value,
  // Compile the project before generating Eclipse files, so that generated .scala or .class files for Twirl templates are present
  EclipseKeys.preTasks := Seq(compile in Compile)
).enablePlugins(SbtWeb, JavaAppPackaging).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(commonSettings).settings(
  scalaJSUseMainModuleInitializer := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.5"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings)
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.12.5",
  organization := "com.github.merlijn"
)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}
