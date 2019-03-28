package com.github.merlijn.baker

import mhtml.Var

object Util {
  def initVarFromCallback[T](initialValue: T, fn: (T => Unit) => Unit): Var[T] = {
    val value = Var(initialValue)
    fn(value := _)
    value
  }
}
