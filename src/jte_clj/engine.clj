(ns jte-clj.engine
  (:require [clojure.java.io :as io]
            [jte-clj.proto :as p])
  (:import [clojure.lang RT]
           [gg.jte CodeResolver ContentType TemplateEngine]
           [java.nio.file Path]
           [java.util Map]))

(def content-types
  {:template/html  ContentType/Html
   :template/plain ContentType/Plain})

(defn resolver* ^CodeResolver [x] (some-> x p/to-resolver))

(defn create
  "Thread-safe object"
  ([resolver]
   (create resolver :template/plain))
  ([resolver content-type]
   (if-some [ct (get content-types content-type)]
     (-> (resolver* resolver)
         (TemplateEngine/create ct))
     (throw
       (IllegalArgumentException.
         (str "Invalid content-type: " content-type))))))

(defn create-precompiled
  ([class-dir]
   (create class-dir :template/plain))
  ([^String class-dir content-type]
   (if-some [ct (get content-types content-type)]
     (-> class-dir
         (Path/of (make-array String 0))
         (TemplateEngine/createPrecompiled ct #_(RT/baseLoader)))
     (throw
       (IllegalArgumentException.
         (str "Invalid content-type: " content-type))))))

(defn generate-sources
  "Compiles all .jte files in <source-dir>
   to .java files into <target-dir> (defaults to
   'src/java/<package-name>'). This fn was
    ported from `jte-maven-plugin`."
  [{:keys [^String source-dir   ;; directory of .jte files
           ^String target-dir   ;; directory of generated .java files
           content-type
           trim-control-structures? ;; results in prettier output
           preserve-html-comments?  ;;
           html-tags
           ^String package-name     ;; the java sources package
           ^Map extensions]
    :or   {target-dir "src/java"
           preserve-html-comments?  true
           trim-control-structures? true
           content-type             :template/plain
           html-tags                []}}]
  {:pre [(contains? content-types content-type)
         (some? source-dir)]}
  (let [engine (doto (TemplateEngine/create
                       (resolver* (io/file source-dir))
                       (Path/of target-dir (make-array String 0))
                       (get content-types content-type)
                       nil
                       package-name)
                 (.setTrimControlStructures trim-control-structures?)
                 (.setHtmlCommentsPreserved preserve-html-comments?)
                 (.setHtmlTags (into-array String html-tags)))]
    ;; extensions is expected to be a Map<String, Map<String, String>>
    ;; The outer String key references the ExtensionSettings::getClassName,
    ;; whereas the inner map references the ExtensionSettings::getSetting
    (some->> (not-empty extensions)
             (.setExtensions engine))
    (.cleanAll engine)
    (-> engine .generateAll vec)))

(comment
  (require '[jte-clj.template :as template]
           '[clojure.java.io :as io])

  (def templates-dir (io/resource "templates"))

  (def engine-plain (create templates-dir :template/plain))
  (def engine-html  (create templates-dir :template/html))

  (def params {:name "dimitris" :city "Manchester"})
  (template/render! engine-plain "hello.jte" params)
  (template/render-to-string engine-plain "hello.jte" params "trying out JTE :)")

  (generate-sources {:source-dir templates-dir
                     :package-name "foo.bar"})
  )