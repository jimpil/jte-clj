(ns jte-clj.proto
  (:import [gg.jte.output FileOutput PrintWriterOutput StringOutput WriterOutput]
           [gg.jte.resolve DirectoryCodeResolver]
           ;[gg.jte Content]
           [java.io File PrintWriter Writer]
           [java.net URI URL]
           [java.nio.file Path]))

(defprotocol ITemplateOutput (to-output [_]))
(extend-protocol ITemplateOutput
  String
  (to-output [this] (doto (StringOutput.) (.writeContent this)))
  Path
  (to-output [this] (FileOutput. this))
  File
  (to-output [this] (to-output (.toPath this)))
  Writer
  (to-output [this] (if (instance? PrintWriter this)
                      (PrintWriterOutput. this)
                      (WriterOutput. this))))

(defprotocol ICodeResolver (to-resolver [_]))
(extend-protocol ICodeResolver
  Path
  (to-resolver [this] (DirectoryCodeResolver. this))
  File
  (to-resolver [this] (to-resolver (.toPath this)))
  URI
  (to-resolver [this] (to-resolver (Path/of this)))
  URL
  (to-resolver [this] (to-resolver (.toURI this))))
