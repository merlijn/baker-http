package com.github.merlijn.baker.components

object Menu {

  def apply(page: String, items: Seq[String], activeItem: String) =
    <div class="well sidebar-nav">
      <ul class="nav nav-list">
        <li class = "nav-header">{ page }</li>
        {
        items.map { name => menuItem(page, name, activeItem) }
        }
      </ul>
    </div>

  def menuItem(page: String, label: String, active: String) = {

    val name = label.toLowerCase()
    val clazz = if (active.equals(name)) Some("active") else None
    val href = s"#$page/$name"

    <li class = { clazz } ><a href = { href }>{ label }</a></li>
  }
}
