package org.jetbrains.plugins.scala.compiler.data

import java.io.File

import org.jetbrains.jps.incremental.scala.{containsDotty, compilerVersionIn}
import org.jetbrains.plugins.scala.compiler.data.CompilerJarsFactory.CompilerJarsResolveError
import org.jetbrains.plugins.scala.util.JarUtil
import org.jetbrains.plugins.scala.util.JarUtil.JarFileWithName

trait CompilerJarsFactory {

  def fromFiles(files: collection.Seq[File]): Either[CompilerJarsResolveError, CompilerJars]
}

object CompilerJarsFactory
  extends CompilerJarsFactory {

  sealed trait CompilerJarsResolveError

  object CompilerJarsResolveError {
    case class NotFound(kind: String) extends CompilerJarsResolveError
    case class DuplicatesFound(kind: String, duplicates: collection.Seq[JarFileWithName]) extends CompilerJarsResolveError
    case class FilesDoNotExist(files: collection.Seq[File]) extends CompilerJarsResolveError
  }

  override def fromFiles(files: collection.Seq[File]): Either[CompilerJarsResolveError, CompilerJars] = {
    val jarFiles = JarUtil.collectJars(files)
    fromJarFiles(jarFiles)
  }

  def fromJarFiles(files: collection.Seq[JarFileWithName]): Either[CompilerJarsResolveError, CompilerJars] = {
    val compilerPrefix = if (containsDotty(files.map(_.file))) "dotty" else "scala"

    val init: Either[CompilerJarsResolveError, Seq[JarFileWithName]] = Right(Seq.empty)
    val libraryJars = Set("scala-library", s"$compilerPrefix-library").foldLeft(init) { (acc, kind) =>
      for {
        jars <- acc
        jar <- find(files, kind)
      } yield jars :+ jar
    }
    for {
      libraries <- libraryJars
      compiler <- find(files, s"$compilerPrefix-compiler")
      extra = files.filterNot { file =>
        file == compiler || libraries.contains(file)
      }
      _ <- scalaReflect(compiler, extra)
    } yield CompilerJars(
      libraries = libraries.map(_.file),
      compiler = compiler.file,
      extra = extra.map(_.file)
    )
  }

  private def find(files: collection.Seq[JarFileWithName], kind: String): Either[CompilerJarsResolveError, JarFileWithName] = {
    val filesOfKind = files.filter(_.name.startsWith(kind)).distinct
    filesOfKind match {
      case collection.Seq(file) => Right(file)
      case collection.Seq() => Left(CompilerJarsResolveError.NotFound(kind))
      case duplicates => Left(CompilerJarsResolveError.DuplicatesFound(kind, duplicates))
    }
  }

  private def scalaReflect(compiler: JarFileWithName, extra: collection.Seq[JarFileWithName]): Either[CompilerJarsResolveError, Unit] =
    if (compilerVersionIn(compiler.file, "2.10")) find(extra, "scala-reflect").map(_ => ())
    else Right(())
}
