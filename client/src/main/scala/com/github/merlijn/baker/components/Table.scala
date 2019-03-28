package com.github.merlijn.baker.components

import scala.xml.Elem

object Table {

  def apply[T](items: Seq[T],
               cellRender: Seq[(String, T => Elem)],
               rowStyle: T => Option[String] = (e: T) => None): Elem = {

    <div>
      <form class="form-inline my-2 my-lg-0">
        <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search" />
        <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
      </form>
      <table class="table">
        <thead class="thead-light">
          <tr>
            { cellRender.map { name => <th scope="col">{ name._1 }</th> } }
          </tr>
        </thead>
        <tbody>
          {
            items.map { i =>
              <tr class= { rowStyle(i) } >
                {
                  cellRender.map {
                    case (_, fn) => <td>{ fn(i) }</td>
                  }
                }
              </tr>
            }
          }
        </tbody>
      </table>
    </div>
  }
}
