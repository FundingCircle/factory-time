(defproject factory-time "0.1.2"
  :description "A Clojure library inspired by factory_girl"
  :url "https://github.com/FundingCircle/factory-time"
  :license {:name "BSD 3-clause"
            :url "http://opensource.org/licenses/BSD-3-Clause"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :profiles {:dev {:dependencies [[speclj "3.3.2"]]}}
  :plugins [[speclj "3.3.2"]]
  :repositories [["releases"
                  {:url "https://clojars.org/repo"
                   :sign-releases false
                   :username [:gpg :env/clojars_user]
                   :password [:gpg :env/clojars_password]}]
                 ["snapshots"
                  {:url "https://clojars.org/repo"
                   :sign-releases false
                   :username [:gpg :env/clojars_user]
                   :password [:gpg :env/clojars_password]}]]
  :test-paths ["spec"])
