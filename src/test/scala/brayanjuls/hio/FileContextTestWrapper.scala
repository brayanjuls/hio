package brayanjuls.hio

import hio.HIOPath
import org.apache.hadoop.fs.FileContext

trait FileContextTestWrapper{

  lazy val fileContext: FileContext = FileContext.getLocalFSFileContext

  lazy val wd:HIOPath = hio.HIOPath(fileContext.getWorkingDirectory) / "hio-tmp"

}
