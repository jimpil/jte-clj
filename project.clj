(defproject jte-clj "0.1.0-SNAPSHOT"
  :description "Lightweight wrapper around JTE (Java Template Engine)"
  :url "https://github.com/jimpil/jte-clj"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [gg.jte/jte "3.2.1"]]
  :repl-options {:init-ns jte-clj.engine})
