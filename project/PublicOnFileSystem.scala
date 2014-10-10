import sbt._
import sbt.Keys._
import play.Play.autoImport._
import com.typesafe.sbt.SbtNativePackager._

object PublicOnFileSystem {

  val settings = Seq(
    mappings in Universal <++= (baseDirectory in Compile) { _ / "public" } map { dir: File =>
      val directoryLen = dir.getCanonicalPath.length
      val pathFinder = dir ** "*"
      pathFinder.get map {
        publicFile: File =>
          publicFile -> ("public/" + publicFile.getCanonicalPath.substring(directoryLen))
        }
    }
  )
}

