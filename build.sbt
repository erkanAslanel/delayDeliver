lazy val akkaHttpVersion = "10.1.10"
lazy val akkaVersion    = "2.5.25"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.erkan",
      scalaVersion    := "2.12.8"
    )),
    name := "delay-deliver",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test,
      
      "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.7",
      "com.typesafe.play" %% "play-ws-standalone-json" % "2.0.7",
      "com.typesafe.play" %% "play-ws-standalone-xml" % "2.0.7"
    )
    

	
  )