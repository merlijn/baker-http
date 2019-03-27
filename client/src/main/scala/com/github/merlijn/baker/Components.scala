package com.github.merlijn.baker

import com.github.merlijn.baker.model.Interaction

object Components {

  import mhtml._

  object Menu {

    def apply(page: String, items: Seq[String], activeItem: String) =
      <div class="well sidebar-nav">
        <ul class="nav nav-list">
          <li class = "nav-header">Catalogue</li>
          {
            items.map { name => menuItem(page, name, activeItem) }
          }
        </ul>
      </div>

    def menuItem(page: String, label: String, active: String) = {

      val name = label.toLowerCase()
      val clazz = if (active.equals(name)) Some("active") else None
      val href = s"#$page/$name"

      <li class = { clazz } ><a href = { href }>{ name }</a></li>
    }
  }

  def interactionsTable(interactions: Seq[Interaction]) =
    <div>
      <form class="form-inline my-2 my-lg-0">
        <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search" />
        <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
      </form>
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
                <td><a href = { s"#catalogue/interactions/${i.name}" }>{ i.name }</a></td>
                <td>{ i.input.map(_.name).mkString(",") } </td>
                <td>{ i.output.map(_.name).mkString(",") } </td>
              </tr>
            }
          }
        </tbody>
      </table>
    </div>

  def topNavigationBar(activePage: String) = {

    val items = Seq("Catalogue", "Interact", "Monitor")

    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="brand" href="#catalogue">Baker</a>
          <div class="nav-collapse collapse">
            <p class="navbar-text pull-right">
              Logged in as <a href="#" class="navbar-link">Anonymous</a>
            </p>
            <ul class="nav">
              {
                items.map { i =>

                  val clazz = if (activePage.equals(i.toLowerCase)) Some("active") else None

                  <li class = { clazz }><a href={ s"#${i.toLowerCase}" }>{ i }</a></li>
                }
              }
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>
  }

}
