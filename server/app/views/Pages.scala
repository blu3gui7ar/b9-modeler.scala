package views

import controllers.routes

import scalatags.Text.all
import scalatags.Text.all._
/**
  * Created by blu3gui7ar on 2016/12/30.
  */
object Pages {

  def bundleUrl(projectName: String, suffix: String = ""): Option[String] = {
//    routes.Assets.versioned("client-" + (if (dev) "fastopt"  else "fullopt") + ".js").toString
    val name = projectName.toLowerCase
    Seq(s"$name-opt$suffix.js", s"$name-fastopt$suffix.js")
      .find(name => getClass.getResource(s"/public/$name") != null)
      .map(routes.Assets.versioned(_).url)
  }

  def npmUrl(npmPath: String): String = {
    routes.Assets.versioned(npmPath).url
  }

  def common(name: String, entry: Option[String], dev: Boolean) = {
    "<!DOCTYPE html>" + all.html(
      head(
        tag("title")(name),
        link(
          rel := "stylesheet",
          href := npmUrl("bulma/css/bulma.css")
        ),
        link(
          rel := "stylesheet",
          href := npmUrl("@fortawesome/fontawesome-free/css/fontawesome.min.css")
        ),
        link(
          rel := "shortcut icon",
          `type` := "image/png",
          href := routes.Assets.versioned("images/favicon.png").toString
        )
      ),
      body()(
        div(id := shared.Apps.ModelerApp),
        for { srcUrl <- bundleUrl("client", "-library") } yield script(
          `type` := "text/javascript",
          src := srcUrl
        ),
        script(
          `type` := "text/javascript",
          "var exports = window;",
          "exports.require = window['ScalaJSBundlerLibrary'].require;"
        ),
        for { srcUrl <- bundleUrl("client") } yield script(
          `type` := "text/javascript",
          src := srcUrl
        ),
//        if(dev) script(
//          `type` := "text/javascript",
//          src := "//localhost:12345/workbench.js"
//        )
//        else (),
        entry.map( str =>
          script(`type` := "text/javascript")(
            "document.body.onload = " + str
          )
        )
      )
    )
  }
}
