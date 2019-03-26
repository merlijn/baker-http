package com.github.merlijn.baker

import com.github.merlijn.baker.model.Interaction

object Components {

  import mhtml._

  def renderInteraction(interactions: Seq[Interaction]) =
    <table class="table">
      <thead>
        <tr>
          <th scope="col">name</th>
          <th scope="col">input</th>
          <th scope="col">output</th>
        </tr>
      </thead>
      <tbody>
        {
          interactions.map { i =>
            <tr>
              <td>{ i.name }</td>
              <td>{ i.input.map(_.name).mkString(",") } </td>
              <td>{ i.output.map(_.name).mkString(",") } </td>
            </tr>
          }
        }
      </tbody>
    </table>

  def foo(): Unit = { }

  def sideBar(active: String, navFn: String => Unit) =
    <div class="well sidebar-nav">
      <p><a href="#" class="btn btn-primary btn-large">Create new recipe</a></p>
      <ul class="nav nav-list">
        <li class = "nav-header">Catalogue</li>
        { sideBarButton("Recipes", active, navFn) }
        { sideBarButton("Interactions", active, navFn) }
        { sideBarButton("Events", active, navFn) }
      </ul>
    </div>

  def sideBarButton(label: String, active: String, navigateFn: String => Unit) = {

    val name = label.toLowerCase()
    val clazz = if (active.equals(name)) "active" else "inactive"
    val href = s"#design/$name"

    <li class = { clazz } >
      <a href = { href } onclick = { () => navigateFn(name) } >{ name }</a>
    </li>
  }

  val navBar =
    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="brand" href="#">Baker</a>
          <div class="nav-collapse collapse">
            <p class="navbar-text pull-right">
              Logged in as <a href="#" class="navbar-link">Anonymous</a>
            </p>
            <ul class="nav">
              <li class="active"><a href="#">Design</a></li>
              <li><a href="#interact">Interact</a></li>
              <li><a href="#monitor">Monitor</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>
}
