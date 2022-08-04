package com.bintray.scala.prettification.diff

import java.io.{File, PrintWriter}
import java.nio.file.{Path, Paths}

import com.bintray.scala.prettification.CaseClassPrettifier

import scala.language.postfixOps

/**
  * When running feature files the diff on error is useless compared to scalatest equivalent (click diff view in intellij)
  * Call one of the following methods prior to failing assertion to see the
  * expectancy within intellij.
  *
  * https://www.jetbrains.com/help/idea/running-the-ide-as-a-diff-or-merge-command-line-tool.html
  *
  * You will need to install the idea shell script
  * (section of "To enable invoking IntelliJ IDEA operations from the command line, follow these steps")
  *
  */
object IdeaDiff {
  private val prettifier = new CaseClassPrettifier

  def viewClass(output: AnyRef): Unit = {
    viewText(prettifier.prettify(output), "txt")
  }

  def viewText(output: String, ext: String = "json"): Unit = {
    val tempFile = createTempFile(output, ext, 1)

    val command =
      s"""
         |idea ${tempFile.getAbsolutePath}
    """.stripMargin.trim

    import sys.process._
    command !
  }

  /**
    * use this one for classes as it will humanify case classes
    */
  def diffClass(output1: AnyRef, output2: AnyRef): Unit = {
    diffText(prettifier.prettify(output1), prettifier.prettify(output2), "txt")
  }

  /**
    * Use this one for any text
    */
  def diffText(output1: String, output2: String, ext: String = "json"): Unit = {
    val tempFile1 = createTempFile(output1, ext, 1)
    val tempFile2 = createTempFile(output2, ext, 2)

    val command =
      s"""
         |idea diff  ${tempFile1.getAbsolutePath} ${tempFile2.getAbsolutePath}
    """.stripMargin.trim

    import sys.process._
    command !
  }

  private def createTempFile(contents: String, ext: String, index: Int): File = {
    //can use create temp file without auto delete but that creates many temp files
    //deleteOnExit ruins the option to view a single file as idea will detect it is deleted
    //before viewing.
    val tempDir: Path = Paths.get(System.getProperty("java.io.tmpdir"))
    val file = new File(tempDir.toAbsolutePath.toString + s"/diff_$index.$ext")

    new PrintWriter(file) {
      try {
        write(contents)
      } finally {
        close()
      }
    }
    file
  }
}
