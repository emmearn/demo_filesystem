package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.files.{DirEntry, Directory}
import com.rtjvm.scala.oop.filesystem.State

abstract class CreateEntry(name: String) extends Command {
  override def apply(state: State): State = {
    val wd = state.wd

    if (wd.hasEntry(name)) {
      state.setMessage(s"Entry $name already exists!")
    } else if(name.contains(Directory.SEPARATOR)) {
      // mkdir -p something/somethingElse
      state.setMessage(name + " must not contain separators!")
    } else if(checkIllegal(name)) {
      state.setMessage(name + ": illegal entry name!")
    } else {
      doCreateEntry(state, name)
    }
  }

  def checkIllegal(name: String): Boolean = {
    name.contains(".")
  }

  def doCreateEntry(state: State, name: String): State = {
    def updateStructure(currentDirectory: Directory, path: List[String], newEntry: DirEntry): Directory = {
      /*
        someDir
          /a
          /b
          (new) /d

        => new someDir
          /a
            /...
          /b
            /...
          /d



        /a/b
          /c
          /d
          (new) /e

        new /a
          new /b (parent /a)
            /c
            /d
            /e
       */

      if(path.isEmpty) currentDirectory.addEntry(newEntry)
      else {
        val oldEntry = currentDirectory.findEntry(path.head).asDirectory
        currentDirectory.replaceEntry(oldEntry.name, updateStructure(oldEntry, path.tail, newEntry))
      }

      /*
        /a/b
          (contents)
          (new entry) /e

        newRoot = updateStructure(root, ["a", "b"], /e) = root.replaceEntry("a", updateStructure(/a, ["b"], /e) = /a.replaceEntry("b", updateStructure(/b, [], /e) = /b.add(/e)
          => path.isEmpty?
          => oldEntry = /a
          root.replaceEntry("a", updateStructure(/a, ["b"], /e) = /a.replaceEntry("b", updateStructure(/b, [], /e) = /b.add(/e)
            => path.isEmpty?
            => oldEntry = /b
            /a.replaceEntry("b", updateStructure(/b, [], /e) = /b.add(/e)
              => path.isEmpty? => /b.add(/e)
       */
    }

    val wd = state.wd

    // 1. all the directory is the full path
    val allDirsInPath = wd.getAllFoldersInPath

    // 2. create new directory entry in the wd
    // TODO implement this
    val newEntry: DirEntry = createSpecificEntry(state)
    // val newDir = Directory.empty(wd.path, name)

    // 3. update the whole directory structure starting from root
    // (the directory structure is IMMUTABLE)
    val newRoot = updateStructure(state.root, allDirsInPath, newEntry)

    // 4. find new working directory INSTANCE given wd's full path in the NEW directory structure
    val newWd = newRoot.findDescendant(allDirsInPath)

    State(newRoot, newWd)
  }

  def createSpecificEntry(state: State): DirEntry
}
