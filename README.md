# Factory Time

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

Factory Time merges data in the following order, from lowest to highest precedence:

1. Parent factory result
1. Factory defaults
1. Generated values
1. Build overrides
1. create! result (skipped when `build` is called)

## License

Copyright © 2015 Funding Circle

Distributed under the BSD 3-Clause License.
