# factory_time (TBD)

Factory time is a Clojure library for maintaining test data, similar to [Fabricator](http://www.fabricationgem.org/) is for Ruby.

## Leiningen

`[factory-time "0.1.0"]`

## Usage

```clojure
(deffactory :parent {:name "Billy Joe", :age 42})
(deffactory :child {:age 12}
  :extends-factory :parent
  :generator-annoying (fn [n] (even? n) # n starts at 1 and will increase by 1 every time function is called
  :create! save-child!)

# First call (n = 1)
# create! will be skipped
(build :child {:hair-color "red"}) # {:name "Billy Joe"
                                      :age 12
                                      :annoying false
                                      :hair-color "red"}

# Second call (n = 2)
# create! will be called
(create! :child {:hair-color "black"}) # {:name "Billy Joe"
                                          :age 12
                                          :annoying true
                                          :hair-color "black"}
```

A Factory Time merges data in the following order, from lowest to highest precedence:

1. Parent factory result
2. Factory defaults
3. Generated values
4. create! result (skipped when ```build``` is called)

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
