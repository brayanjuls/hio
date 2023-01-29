package hio

import org.apache.hadoop.fs.{FileContext, FileStatus, Path => HadoopPath}

import java.net.URI
import scala.annotation.tailrec

/**
 * Represent each part of a file path. ie: home in the path root/user/mji/home
 */
trait PathPart{
  def segments: Seq[String]
}

/**
 * Type Enrichment
 */
object PathPart{
  implicit class StringPathPart(s:String) extends PathPart {
    override def segments: Seq[String] = Seq(s)
    override def toString: String = s
  }

  implicit class URIPathPart(s:URI) extends PathPart{
    override def segments: Seq[String] = Seq(s.toString)

    override def toString: String = s.toString
  }
}

trait BasePath{

  type ThisType <: BasePath
  def /(value:PathPart):ThisType

  def ext:String

  def lastName:String

  def baseName:ThisType
}



/**
 * absolute file path
 * fully-qualified URI: scheme://authority/path
 * (e.g.hdfs://nnAddress:nnPort/foo/bar)
 * @param path
 */
class HIOPath private[hio](val path:HadoopPath) extends BasePath {
  override type ThisType = HIOPath

  override def /(value: PathPart): ThisType = {
    val concatenatedPath = new HadoopPath(path,value.toString)
    new HIOPath(concatenatedPath)
  }

  override def ext: String = {
    val separatorIndex = path.getName.lastIndexOf(HIOPath.PERIOD_SEPARATOR)
    if(separatorIndex>0)
      path.getName.substring(separatorIndex+1)
    else ""
  }
  override def lastName: String = path.getName
  override def baseName: HIOPath = {
    @tailrec
    def getRoot(hadoopPath:HadoopPath):HIOPath ={
      if(hadoopPath.isRoot){
        new HIOPath(hadoopPath)
      }else if(hadoopPath.getParent.isRoot){
        new HIOPath(hadoopPath)
      }else{
        getRoot(hadoopPath.getParent)
      }
    }
    getRoot(path)
  }

  //TODO: add validation when the home or wd is tried to be get but the fileSystem does not exits or is incorrect
  def home: HIOPath = new HIOPath(FileContext.getFileContext(path.toUri).getHomeDirectory)

  def wd:HIOPath = new HIOPath(FileContext.getFileContext(path.toUri).getWorkingDirectory)

  def uri:URI = path.toUri

  def relativePath:HadoopPath = {
    val pathValue = path.toUri.getPath
    if(pathValue.nonEmpty)
       new HadoopPath(pathValue)
    else new HadoopPath(HIOPath.SLASH_SEPARATOR)
  }

  override def toString: String = path.toUri.toString
}

object HIOPath{

  private val PERIOD_SEPARATOR = "."
  private val SLASH_SEPARATOR = "/"
  def apply[T:ConvertiblePath](value:T):HIOPath = {
    val newPath = implicitly[ConvertiblePath[T]].apply(value)
    new HIOPath(newPath)
  }
}

/**
 * Type class used to create HadoopPaths from different types (String, Path, etc)
 * @tparam T
 */
sealed trait ConvertiblePath[T]{
  def apply(v: T): HadoopPath
}

object ConvertiblePath{
  implicit object StringToHadoopPath extends ConvertiblePath[String] {
    override def apply(v: String): HadoopPath = {
      //TODO: ADD VALIDATION WHEN v is empty, the Hadoop path class will not let you create an object from an empty string
      new HadoopPath(v)
    }
  }

  implicit object FileStatusToHadoopPath extends ConvertiblePath[FileStatus] {
    override def apply(v: FileStatus): HadoopPath = v.getPath
  }

  implicit object HadoopPathToSame extends ConvertiblePath[HadoopPath]{
    override def apply(v: HadoopPath): HadoopPath = v
  }

  implicit object URIToHadoopPath extends ConvertiblePath[URI]{
    override def apply(v: URI): HadoopPath = new HadoopPath(v)
  }
}

