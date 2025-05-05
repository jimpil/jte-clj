(ns jte-clj.template-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [jte-clj.template :as template]
            [jte-clj.engine :as engine]))

(def engine-plain
  (delay
    (-> "test/resources/templates"
        io/file
        (engine/create :template/plain))))

(deftest render-string-tests
  (testing "rendering to empty String"
    (let [params {:name "dimitris" :city "Manchester"}
          rendered (template/render-to-string @engine-plain "hello.jte" params "")
          expected "\nHello Manchester - my name is dimitris!"]
      (is (= rendered expected))))

  (testing "rendering to non-empty String (i.e. appending)"
    (let [params {:name "dimitris" :city "Manchester"}
          rendered (template/render-to-string @engine-plain "hello.jte" params "trying out JTE :)")
          expected "trying out JTE :)\nHello Manchester - my name is dimitris!"]
      (is (= rendered expected)))))
