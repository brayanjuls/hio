# HIO
This library provides elegant functions to manage hdfs filesystem and cloud object stores.
## Setup

## Functions Documentation
### Configuration
for authentication, you can use environment variables or provide an xml config file:
* Config File
    * To set a configuration file you should follow the official hadoop documentation
        * [Hadoop General](https://hadoop.apache.org/docs/r3.2.1/hadoop-project-dist/hadoop-common/AdminCompatibilityGuide.html#XML_Configuration_Files)
        * [AWS](https://hadoop.apache.org/docs/stable/hadoop-aws/tools/hadoop-aws/index.html#Authentication_properties)
        * [AZURE](https://hadoop.apache.org/docs/stable/hadoop-azure/index.html#Configuring_Credentials)
    * And set the environment variable **CONFIG_PATH** with the file path where the configuration file is stored
    * S3 Example:
      ```xml
      <configuration>
        <property>
            <name>fs.s3a.access.key</name>
            <value>AWS access key ID</value>
        </property>
        <property>
            <name>fs.s3a.secret.key</name>
            <value>AWS secret key</value>
        </property>
      </configuration>
      ```
* Environment Variables
    * AWS
        * AWS_ACCESS_KEY_ID
        * AWS_SECRET_ACCESS_KEY
    * Azure Object Store / Data lake
        * AZURE_TENANT_ID
        * AZURE_CLIENT_ID
        * AZURE_CLIENT_SECRET

### Create Folder
This function creates a folder in a filesystem/object store based on a given name, the folder name it
should follow the rules of the provider.

```scala
val root = hio.Path("s3a://bucket_name")
hio.mkdir(root / "path/to/folder")
```

### List Files/Folders
The function search in the given folder and returns the paths of every file or folder within it.
It also supports searching given a wildcard.

```scala
val wd = hio.Path("s3a://bucket_name/path/to/folders")
hio.ls(wd)
```
or to search given a wildcard
```scala
val wd = hio.Path("s3a://bucket_name/path/to/folders")
hio.ls.withWildCard(wd / "*.txt")
```

The execution of the function will return an array of string containing the paths as follows:
```scala
ArraySeq(
  "s3a://bucket_name/path/to/folders/file_1.txt", 
  "s3a://bucket_name/path/to/folders/file_2.txt", 
  "s3a://bucket_name/path/to/folders/new_folder")
```

Note that if you use the wildcard function with only a directory you will not get all the files and folders within it,
instead it will return only the given folder. e.g:

```scala
val wd = hio.Path("s3a://bucket_name/path/to/folders")
hio.ls.withWildCard(wd)
```

returns

```scala
ArraySeq("s3a://bucket_name/path/to/folders")
```


### Delete Files/Folders
The function `remove` permanently deletes files or folders from a filesystem/object store. It is also possible
to recursively deletes sub-folders/files using `remove.all`. e.g:

```scala
val filePath = hio.Path("s3a://bucket_name/path/to/file")
hio.remove(filePath)
```
or recursively deletes
```scala
val folderPath = hio.Path("s3a://bucket_name/path/to/folder")
hio.remove.all(folderPath)
```

### Copy Files
The function `copy` creates a copy of the files in the source folder in the destination folder. It is also possible
to use this function with wild card `copy.withWildCard`. e.g:

```scala
val src = hio.Path("s3a://bucket_name/path/to/src")
val dest = hio.Path("s3a://bucket_name/path/to/dest")
hio.copy(src,dest)
```
or use it with wildcard
```scala
val src = hio.Path("s3a://bucket_name/path/to/src/*.parquet")
val dest = hio.Path("s3a://bucket_name/path/to/dest")
hio.copy.withWildCard(src,dest)
```

### Move Files
The function `move` creates a copy of the files in the source folder in the destination folder and remove the files
from the source folder. It is also possible to use this function with wild card `move.withWildCard`. e.g:

```scala
val src = hio.Path("s3a://bucket_name/path/to/src")
val dest = hio.Path("s3a://bucket_name/path/to/dest")
hio.move(src,dest)
```
or use it with wildcard
```scala
val src = hio.Path("s3a://bucket_name/path/to/src/*.parquet")
val dest = hio.Path("s3a://bucket_name/path/to/dest")
hio.move.withWildCard(src,dest)
```

### Create Files
This function `write` creates a file in a filesystem from an array of bytes or a string. To create the file
the folder must exist.

```scala
val fileContentInStr =
  """
    |name,lastname,age
    |Maria,Willis,36
    |Benito,Jackson,28
    |""".stripMargin
val wd = hio.Path("s3a://bucket_name/path/to/folders/data.csv")
hio.write(wd,fileContentInStr)
```

### Read Files
This function reads a file from the filesystem/object store and return its representation in
byte array or string.

```scala
val wd = hio.Path("s3a://bucket_name/path/to/folders")
hio.read(wd / "file_1.txt")
```
or to automatically parse to string
```scala
val wd = hio.Path("s3a://bucket_name/path/to/folders")
hio.read.string(wd / "file_1.txt")
```

## How to contribute 
We welcome contributions to this project, to contribute checkout our [CONTRIBUTING.md](CONTRIBUTING.md) file.

## How to build the project

### pre-requisites
* SBT 1.8.2
* Java 8
* Scala 2.12.12

### Building

To compile, run 
`sbt compile`

To test, run
`sbt test`

To generate artifacts, run
`sbt package`