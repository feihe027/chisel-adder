
scalaVersion := "2.13.10"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-language:reflectiveCalls",
)


val chiselVersion = "3.6.0"
addCompilerPlugin("edu.berkeley.cs" %% "chisel3-plugin" % chiselVersion cross CrossVersion.full)
libraryDependencies += "edu.berkeley.cs" %% "chisel3" % chiselVersion
libraryDependencies += "edu.berkeley.cs" %% "chiseltest" % "0.6.2"
libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.0"  // SLF4J API
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.6"  // Logback implementation
