package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.filesystem.State

import scala.annotation.tailrec

class Echo(args: Array[String]) extends Command {
  override def apply(state: State): State = {
    if(args.isEmpty) state
    else if(args.length == 1) state.setMessage(args(0))
    else {
      val operator = args(args.length - 2)
      val filename = args(args.length - 1)
      val contents = createContent(args, args.length - 2)

      if(">>".equals(operator))
        doEcho(state, contents, filename, true)
      else if(">".equals(operator))
        doEcho(state, contents, filename, false)
      else
        state.setMessage(createContent(args, args.length))
    }
  }

  def doEcho(state: State, contents: String, filename: String, append: Boolean) = {
    ???
  }

  // topIndex NON-INCLUSIVE!
  def createContent(args: Array[String], topIndex: Int): String = {
    @tailrec
    def createContentHelper(currentIndex: Int, accumulator: String): String =
      if(currentIndex >= topIndex) accumulator
      else createContentHelper(currentIndex + 1, s"$accumulator ${args(currentIndex)}")

    createContentHelper(0, "")
  }
}
