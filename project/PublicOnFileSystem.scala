import sbt._
import sbt.Keys._
import play.Keys.playAssetsDirectories
import com.typesafe.sbt.SbtNativePackager._

object PublicOnFileSystem {

  val settings = Seq(
    mappings in Universal <++= playAssetsDirectories map { directories: Seq[File] =>
      directories.flatMap { dir: File =>
        val directoryLen = dir.getCanonicalPath.length
        val pathFinder = dir ** "*"
        pathFinder.get map {
          publicFile: File =>
            publicFile -> ("public/" + publicFile.getCanonicalPath.substring(directoryLen))
        }
      }
    }
  )
}

