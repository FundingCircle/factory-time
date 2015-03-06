(defproject factory-time "0.1.1"
  :description "A clojure repository inspired by factory_girl"
  :url "https://github.com/FundingCircle/factory-time"
  :license {:name "BSD 3-clause"
            :url "http://opensource.org/licenses/BSD-3-Clause"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:dependencies [[speclj "3.1.0"]]}}
  :plugins [[speclj "3.1.0"]]
  :test-paths ["spec"])
