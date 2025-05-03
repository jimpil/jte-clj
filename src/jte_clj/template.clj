(ns jte-clj.template
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [jte-clj.proto :as p])
  (:import [gg.jte Content TemplateEngine TemplateOutput]
           [java.util Map]))

(def valid-param? (some-fn (partial instance? Content) string? number? char?))

(defn invalid-params [m] (keep (fn [[k v]] (when-not (valid-param? v) k)) m))
(defn output* ^TemplateOutput [x] (some-> x p/to-output))

(defn render!
  "Renders the template found at <file-in> (slotting in named <params>) to <output>,
   which should be an instance of String/Path/File/Writer (defaults to `*out*`).
   For String outputs in particular, you probably want `render-to-string` instead.
   For anything other than Strings, what this function returns (i.e. `TemplateOutput`)
   can be ignored. The values in the <params> map are checked for type-validity prior
   to calling `TemplateEngine::render`. The keys are converted to String
   (via `clojure.core/name`), so the template file (.jte) should declare them as such."
  ([ngn file-in params]
   (render! ngn file-in params *out*))
  ([^TemplateEngine ngn ^String file-in params output]
   (if-some  [invalid-keys (not-empty (invalid-params params))]
     (throw
       (IllegalArgumentException.
         (str "Unsupported param-type(s) detected: "
              (str/join \, invalid-keys))))
     (let [out (output* output)
           ^Map params (update-keys params name)]
       (.render ngn file-in params  out)
       out))))

(defn render-to-string
  "Like `render!` but returns a String - not a `TemplateOutput`."
  (^String [ngn file-in params]
   (render-to-string ngn file-in params ""))
  (^String [ngn file-in params s]
   (str (render! ngn file-in params s))))
