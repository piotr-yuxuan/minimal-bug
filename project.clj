(defproject minimal-bug "0.1.0-SNAPSHOT"
  :dependencies [[com.brunobonacci/safely "0.5.0"]
                 [clj-python/libpython-clj "1.44"]
                 [org.clojure/clojure "1.10.1"]]
  :main ^:skip-aot core
  :javac-options ["-target" "1.8" "-source" "1.8"]
  :source-paths ["src/clojure"])
