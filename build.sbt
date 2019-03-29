import sbtcrossproject.{crossProject, CrossType}

val bakerVersion = "3.0.0-SNAPSHOT"
val akkaVersion = "2.5.21"
val circeVersion = "0.11.1"

val bakerRuntime =              "com.ing.baker"              %% "baker-runtime"                      % bakerVersion
val bakerCompiler =             "com.ing.baker"              %% "baker-compiler"                     % bakerVersion

val akkaStream =                "com.typesafe.akka"          %% "akka-stream"                        % akkaVersion
val akkaDiscovery =             "com.typesafe.akka"          %% "akka-discovery"                     % akkaVersion
val akkaManagementCluster =     "com.lightbend.akka.management" %% "akka-management-cluster-http"      % "1.0.0"
val akkaClusterBoostrap =       "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "1.0.0"
val akkaDiscoveyKubernetes =    "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api"      % "1.0.0"
val akkaPersistenceCassandra =  "com.typesafe.akka"          %% "akka-persistence-cassandra"         % "0.93"
val logbackElasticSearch =      "com.internetitem"           % "logback-elasticsearch-appender"      % "1.6"
val akkaHttp =                  "com.typesafe.akka"          %% "akka-http"                          % "10.1.1"
val scalaJsScripts =            "com.vmunier"                %% "scalajs-scripts"                    % "1.1.2"
val graphVizJava =              "guru.nidi"                  %  "graphviz-java"                      % "0.8.3"
val scalaTags =                 "com.lihaoyi"                %% "scalatags"                          % "0.6.7"
val betterFiles =               "com.github.pathikrit"       %% "better-files"                       % "3.7.1"
val logback =                   "ch.qos.logback"             %  "logback-classic"                    % "1.2.2"

lazy val sharedDeps = Def.setting(Seq(
  "io.circe" %%% "circe-core" % circeVersion,
  "io.circe" %%% "circe-parser" % circeVersion,
  "io.circe" %%% "circe-generic" % circeVersion
))

lazy val server = (project in file("server")).settings(commonSettings).settings(
  dockerAlias := dockerAlias.value.copy(name = "baker-http-server"),
  moduleName := "baker-http-server",
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= sharedDeps.value,
  libraryDependencies ++= Seq(
    scalaJsScripts,
    scalaTags,
    akkaHttp,
    akkaStream,
    akkaDiscovery,
    akkaDiscoveyKubernetes,
    akkaClusterBoostrap,
    akkaManagementCluster,
    akkaPersistenceCassandra,
    logbackElasticSearch,
    bakerRuntime,
    bakerCompiler,
    graphVizJava,
    logback
  ),
  WebKeys.packagePrefix in Assets := "js/",
  managedClasspath in Runtime += (packageBin in Assets).value,
  // Compile the project before generating Eclipse files, so that generated .scala or .class files for Twirl templates are present
//  EclipseKeys.preTasks := Seq(compile in Compile)
).enablePlugins(SbtWeb, JavaAppPackaging).
  dependsOn(sharedJvm)

lazy val client = (project in file("client")).settings(commonSettings).settings(
  moduleName := "baker-http-client",
  scalaJSUseMainModuleInitializer := true,
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.5",
    "in.nvilla" %%% "monadic-html" % "0.4.0-RC1"
  )
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(commonSettings)
  .settings(
    moduleName := "baker-http-shared",
    libraryDependencies ++= sharedDeps.value
  )

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  dockerExposedPorts := Seq(8080),
  scalaVersion := "2.12.5",
  organization := "com.github.merlijn"
)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}
