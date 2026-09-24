ThisBuild / tlBaseVersion := "0.1" // your current series x.y

ThisBuild / organization := "io.chrisdavenport"
ThisBuild / organizationName := "Christopher Davenport"
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers := List(
  // your GitHub handle and name
  tlGitHubDev("christopherdavenport", "Christopher Davenport")
)

ThisBuild / tlCiReleaseBranches := Seq("main")

val catsV = "2.9.0"
val catsEffectV = "3.4.9"

val munitCatsEffectV = "2.0.0-M3"

ThisBuild / crossScalaVersions := Seq("2.12.15","2.13.18", "3.3.8")
ThisBuild / scalaVersion := "3.3.8"
ThisBuild / versionScheme := Some("early-semver")

ThisBuild / testFrameworks += new TestFramework("munit.Framework")

// Projects
lazy val `fiberlocal` = tlCrossRootProject
  .aggregate(core)

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "fiberlocal",
    libraryDependencies ++= Seq(
      "org.typelevel"               %%% "cats-core"                  % catsV,
      "org.typelevel"               %%% "cats-effect"                % catsEffectV,
      "org.typelevel"               %%% "munit-cats-effect"          % munitCatsEffectV         % Test,
    )
  ).jsSettings(
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule)},
  ).nativeSettings(
    tlVersionIntroduced := List("2.12", "2.13", "3").map(_ -> "0.1.2").toMap
  )

lazy val site = project.in(file("site"))
  .enablePlugins(TypelevelSitePlugin)
  .settings(
    laikaTheme := tlSiteHelium.value.site
      .topNavigationBar(
        homeLink = laika.helium.config.IconLink.internal(laika.ast.Path.Root / "index.md", laika.helium.config.HeliumIcon.home)
      )
      .build
  )
  .dependsOn(core.jvm)
