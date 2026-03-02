[![Build Status](https://travis-ci.org/CIResearchGroup/jbsdiff.svg?branch=master)](https://travis-ci.org/CIResearchGroup/jbsdiff)
jbsdiff
=======
A Java implementation of bsdiff (http://www.daemonology.net/bsdiff/)


Usage
-----
jbsdiff can be used from its command line interface:

java -jar jbsdiff.jar command oldfile newfile patchfile

Where *command* is either 'diff' or 'patch.'  You can also specify the
compression algorithm used during a diff operation by setting a system property:

java -Djbsdiff.compressor=gz -jar jbsdiff.jar diff a.bin b.bin patch.gz

Supported compression algorithms (from the Apache Commons Compress library) are
bzip2 (the default), gz, pack200, and xz.

...but jbsdiff is mostly intended to be used as a library.  See the _ui_ package
for usage examples.

Compiling
---------
To build a local fat jar, run:
```
mvn clean package
```

Dependency Information
----------------------
Available on Maven Central:
```
<dependency>
  <groupId>io.sigpipe</groupId>
  <artifactId>jbsdiff</artifactId>
  <version>1.0</version>
</dependency>
```

Also available at

[![Clojars Project](http://clojars.org/org.jbsdiff/jbsdiff/latest-version.svg)](http://clojars.org/org.jbsdiff/jbsdiff)
