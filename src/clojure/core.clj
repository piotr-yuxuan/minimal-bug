(ns core
  (:require [clojure.java.shell :refer [sh]]
            [clojure.string :as str]
            [libpython-clj.python :as py]
            [libpython-clj.require :as pyr]
            [safely.core :refer [safely-fn]])
  (:import (java.util.concurrent Executors ExecutorService)))

(py/initialize!)
(pyr/require-python 'sys)

(let [base-path (str/trim (:out (sh "pwd")))]
  (.append sys/path (str base-path "/src/python")))

(defn thunk-fn
  [f]
  (fn []
    (println "before in thunk")
    (try (let [ret ((py/as-jvm f))]
           (println "after in thunk")
           ret)
         (catch Throwable ex
           (println "ex" ex)
           (println "ex-map" (Throwable->map ex))
           (println "ex-data" (ex-data ex))
           (throw ex)))))

(defn safely-clj-fail
  [f]
  (safely-fn
    (thunk-fn f)
    :default "failure-value"
    :max-retries 0
    :retry-delay [:fix 10]
    :circuit-breaker :python-circuit-breaker
    :timeout 100))

(defn safely-clj-no-fail
  [f]
  (safely-fn
    (thunk-fn f)
    :default "failure-value"
    :max-retries 0
    :retry-delay [:fix 10]
    ;:circuit-breaker :python-circuit-breaker
    :timeout 100))

(def ^ExecutorService pool
  (Executors/newFixedThreadPool 4))

(defn new-thread-fail
  [f]
  (let [thunk (thunk-fn f)]
    (deref
      (.submit
        pool
        ^Callable
        (fn []
          (try
            (thunk)
            (catch Throwable x
              (println "Throwable!")
              x))))
      1000
      :timeout)))

(defn new-thread-hang
  [f]
  (let [thunk (thunk-fn f)]
    (deref
      (.submit
        pool
        ^Callable
        (fn hung-thunk []
          (try
            (thunk)
            (catch Throwable x
              (println "Throwable!")
              x)))))))

(defn same-thread-no-fail
  [f]
  (let [thunk (thunk-fn f)]
    (try
      (thunk)
      (catch Throwable x
        (println "Throwable!")
        x))))

(defn simple-thread-think-no-fail
  [f]
  (let [thunk (thunk-fn #(println "Hello, world!"))]
    (try
      (thunk)
      (catch Throwable x
        (println "Throwable!")
        x))))

(defn vanilla-thread-no-fail
  [f]
  (.run
    (Thread.
      (fn []
        (try
          (println ((py/as-jvm f)))
          (catch Throwable x
            (println "Throwable!" x)
            x))))))

(defn attempt
  [mode]
  (py/set-attrs!
    (py/import-module "ClojureBridge.ClojureBridge")
    [["safely_clj" (cond (= "safely-clj-fail" mode)
                         (py/make-tuple-instance-fn safely-clj-fail)

                         (= "safely-clj-no-fail" mode)
                         (py/make-tuple-instance-fn safely-clj-no-fail)

                         (= "new-thread-fail" mode)
                         (py/make-tuple-instance-fn new-thread-fail)

                         (= "new-thread-hang" mode)
                         (py/make-tuple-instance-fn new-thread-hang)

                         (= "same-thread-no-fail" mode)
                         (py/make-tuple-instance-fn same-thread-no-fail)

                         (= "simple-thread-think-no-fail" mode)
                         (py/make-tuple-instance-fn simple-thread-think-no-fail)

                         (= "vanilla-thread-no-fail" mode)
                         (py/make-tuple-instance-fn vanilla-thread-no-fail))]])
  (py/py.. (py/import-module "MinimalBug") (call_me_baby)))

(defn -main
  [mode]
  (attempt mode)
  (System/exit 1))
