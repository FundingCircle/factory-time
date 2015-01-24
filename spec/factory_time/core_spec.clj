(ns factory-time.core-spec
  (:require [factory-time.core :refer :all]
            [speclj.core :refer :all]))

(deffactory :book {:author "Joe Abercrombie"})
(deffactory :person {:name "Alex" :favorites {:food "steak" :color "red"}})

(describe "factory-time.core"
  (describe "build"
    (it "builds the base object"
      (should= {:author "Joe Abercrombie"}
               (build :book)))

    (it "overrides properties"
      (should= {:author "Brent Weeks"}
               (build :book {:author "Brent Weeks"})))

    (it "adds new properties"
      (should= {:author "Joe Abercrombie"
                :title "The Blade Itself"}
               (build :book {:title "The Blade Itself"})))
    
    (it "merges nested maps"
      (should= {:food "steak" :color "green"}
               (:favorites (build :person {:favorites {:color "green"}}))))))
