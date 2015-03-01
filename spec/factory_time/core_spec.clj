(ns factory-time.core-spec
  (:require [factory-time.core :refer :all]
            [speclj.core :refer :all]))

(deffactory :book {:author "Joe Abercrombie"})
(deffactory :person {:name "Alex" :favorites {:food "steak" :color "red"}})
(deffactory :full-book {:isbn "123"}
  :extends-factory :book)
(deffactory :generating-book {}
  :extends-factory :book
  :generators {:isbn str
               :title (fn [i] (str "Book " i))})

(def saved-food (atom nil))
(deffactory :food {:name "pie"}
  :create! (fn [food]
             (reset! saved-food food)))

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
      (should= [{:isbn "1" :title "Book 1"} {:isbn "2" :title "Book 2"}]
               (map #(select-keys % [:isbn :title]) [(build :generating-book) (build :generating-book)])))

    (it "overrides generated properties"
      (should= "banana"
               (:isbn (build :generating-book {:isbn "banana"})))))

  (describe "create!"
    (with! result (create! :food))

    (it "persists the food"
      (should= {:name "pie"}
               @saved-food))

    (it "creates the object"
      (should= {:name "pie"}
               @result))

    (it "throws an exception when create! is not defined"
      (should-throw
        (create! :book)))))
