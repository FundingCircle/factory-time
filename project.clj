(defproject factory_time "0.1.0"
  :description "A clojure repository inspired by factory_girl"
  :url "https://github.com/FundingCircle/factory-time"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:dependencies [[speclj "3.1.0"]]}}
  :plugins [[speclj "3.1.0"]]
  :test-paths ["spec"])
