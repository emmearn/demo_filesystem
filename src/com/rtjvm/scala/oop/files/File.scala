package com.rtjvm.scala.oop.files

import com.rtjvm.scala.oop.filesystem.FilesystemException

class File(override val parentPath: String, override val name: String, contests: String) extends DirEntry(parentPath, name) {
  override def asDirectory: Directory =
    throw new FilesystemException("A file cannot be converted to a directory!")

  override def asFile: File = this

  override def getType: String = "File"
}

object File {
  def empty(parenthPath: String, name: String): File =
    new File(parenthPath, name, "")
}