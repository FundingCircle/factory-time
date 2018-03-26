# Factory Time
[![Circle CI](https://circleci.com/gh/FundingCircle/factory-time/tree/master.svg?style=svg)](https://circleci.com/gh/FundingCircle/factory-time/tree/master)
[![Downloads](https://jarkeeper.com/FundingCircle/factory-time/downloads.svg)](https://jarkeeper.com/FundingCircle/factory-time)
[![Dependencies Status](https://jarkeeper.com/FundingCircle/factory-time/status.svg)](https://jarkeeper.com/FundingCircle/factory-time)

Factory Time is a Clojure library for maintaining test data, similar to [Fabricator](http://www.fabricationgem.org/) for Ruby. Read more about the motivation for Factory Time on our [blog](https://engineering.fundingcircle.com/blog/2015/03/21/factory-time/).

## Leiningen

`[factory-time "0.1.2"]`

## Usage

```clojure
; person_factory.clj
(ns person-factory
 (:require [factory-time.core :refer :all]))

(deffactory :person {:name "Billy Joe", :age 42})
(deffactory :child {:age 12}
  :extends-factory :person
  :generators {:annoying (fn [n] (even? n)} ; n starts at 1 and will increase by 1 every time build is called
  :create! save-child!)
```

```clojure

; person_spec.clj
(ns person-spec
 (:require [factory-time.core :refer [build create!]]))

# First call (n = 1)
# save-child! will be skipped
(build :child {:hair-color "red"}) ; {:name "Billy Joe"
                                   ;  :age 12
                                   ;  :annoying false
                                   ;  :hair-color "red"}

# Second call (n = 2)
# save-child! will be called
(create! :child {:hair-color "black"}) ; {:name "Billy Joe"
                                       ;  :age 12
                                       ;  :annoying true
                                       ;  :hair-color "black"}
```

The key overrides strategy can be customized with [meta-merge](https://github.com/weavejester/meta-merge).

```clojure
(deffactory :person {:name "Alex" :favorites {:food "steak" :color "red"}})

;; By default, merges maps
(build :person {:favorites {:color "green"}})  
;; {:name "Alex" :favorites {:food "steak" :color "green"}}

;; With ^:replace
(build :person {:favorites ^:replace {:color "green"}}) 
;; {:name "Alex" :favorites {:color "green"}}
```

Factory Time merges data in the following order, from lowest to highest precedence:

1. Parent factory result
1. Factory defaults
1. Generated values
1. Build overrides
1. create! result (skipped when `build` is called)


## License

Copyright © 2015 Funding Circle

Distributed under the BSD 3-Clause License.
