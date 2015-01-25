(ns factory-time.core-spec
  (:require [factory-time.core :refer :all]
            [speclj.core :refer :all]))

(def isbn-count (atom 0))

(deffactory :book {:author "Joe Abercrombie"})
(deffactory :person {:name "Alex" :favorites {:food "steak" :color "red"}})
(deffactory :full-book {:isbn "123"}
  :extends-factory :book)
(deffactory :generating-book {}
  :extends-factory :book
  :generator-isbn str)
(deffactory :mapped-generating-book {}
  :extends-factory :book
  :generator-isbn str
  :generate (fn [gen-values]
              {:renamed-isbn (:isbn gen-values)}))

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
               (:favorites (build :person {:favorites {:color "green"}}))))

    (it "adds properties from parent factory"
      (should= {:author "Joe Abercrombie"
                :isbn "123"}
               (build :full-book)))

    (it "generates properties"
      (should= ["1" "2"]
               (map :isbn [(build :generating-book) (build :generating-book)])))

    (it "overrides generated properties"
      (should= "banana"
               (:isbn (build :generating-book {:isbn "banana"}))))

    (it "maps generated values"
      (should= {:renamed-isbn "1" :author "Joe Abercrombie"}
               (build :mapped-generating-book)))))
