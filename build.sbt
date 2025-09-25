import org.openurp.parent.Settings.*

ThisBuild / organization := "org.openurp.lab.experiment"
ThisBuild / version := "0.0.2-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/lab-experiment"),
    "scm:git@github.com:openurp/lab-experiment.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Edu Extern"
ThisBuild / homepage := Some(url("http://openurp.github.io/lab-experiment/index.html"))

val apiVer = "0.47.0"
val starterVer = "0.4.1"
val baseVer = "0.4.56"
val eduCoreVer = "0.3.18"
val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_lab_api = "org.openurp.lab" % "openurp-lab-api" % apiVer
val openurp_edu_core = "org.openurp.edu" % "openurp-edu-core" % eduCoreVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer

lazy val webapp = (project in file("."))
  .enablePlugins(WarPlugin, TomcatPlugin, UndertowPlugin)
  .settings(
    name := "openurp-lab-experiment-webapp",
    common,
    libraryDependencies ++= Seq(openurp_edu_api, openurp_lab_api, openurp_stater_web, openurp_edu_core),
    libraryDependencies ++= Seq(openurp_stater_web, openurp_base_tag)
  )

