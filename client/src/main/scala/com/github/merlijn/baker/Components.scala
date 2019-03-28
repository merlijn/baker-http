package com.github.merlijn.baker

import mhtml.{Rx, Var}

import scala.xml.Elem

object Components {

  val noContent: Rx[Elem] = Var(<div></div>)

  def topNavigationBar(activePage: String) = {

    val items = Seq("Catalogue", "Monitor")

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
              <a href = "https://www.github.com/merlijn/baker-http">
                <img src="/assets/img/Github-Mark-32px.png" />
                <span style="padding-left: 10px">Github code</span>
              </a>
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
