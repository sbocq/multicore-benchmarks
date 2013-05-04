name := "MoleculeVsGo"

version := "0.1"

scalaVersion := "2.9.3"

resolvers ++= Seq(
    "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases")

libraryDependencies += "com.github.sbocq" %% "mbench" % "0.2.4"

libraryDependencies += "com.github.molecule-labs" %% "molecule-io" % "0.5"

fork := true

javaOptions <++= (fullClasspath in Runtime).map(cp =>
    Seq("-cp", cp.files.mkString(System.getProperty("path.separator")),
        "-XX:+UseNUMA", "-XX:+UseCondCardMark", "-Xss1M" , "-XX:MaxPermSize=128m",
        "-XX:+UseParallelGC", "-XX:+DoEscapeAnalysis", "-Xms1024m", "-Xmx1024m",
        "-Dmbench.log.stdout=true"))
