(ns factory-time.core)

(defmulti get-factory identity)

(defn build
  ([factory-name] (build factory-name {}))
  ([factory-name overrides]
   (let [factory (get-factory factory-name)]
     (.build-poop factory overrides))))

(defn- merge-or-replace [a b]
  (if (every? map? [a b])
    (merge a b)
    b))

(defn- generate-key? [key]
  (-> key
    name
    (.startsWith "generator-")))

(defn- get-name [key]
  (->> key
    name
    (drop 10) ; 'generator-' is 10 chars
    clojure.string/join
    keyword))

(defn- create-generator [config key]
  {:name (get-name key)
   :counter (atom 0)
   :map-fn (key config)})

(defn extract-generators [config]
  (let [generator-keys (filter generate-key? (keys config))
        generators (map (partial create-generator config) generator-keys)]
    (vec generators)))

(defn- build-generated-values [generators]
  (loop [generators generators
         acc {}]
    (if (empty? generators)
      acc
      (let [{:keys [name counter map-fn]} (first generators)
            current-count (swap! counter inc)
            generated-value (map-fn current-count)]
        (recur (rest generators) (assoc acc name generated-value))))))

(defprotocol Builder
  (build-poop [this overrides]))

(defrecord Factory [config]
  Builder
  (build-poop [this overrides]
    (let [parent-builder (if (contains? (:config this) :extends-factory)
                           (partial build (get-in this [:config :extends-factory]))
                           (fn [] {}))
          generate-fn (get-in this [:config :generate] identity)
          generated-values (build-generated-values (get-in this [:config :generators]))]
      (merge-with merge-or-replace
                  (parent-builder)
                  (get-in this [:config :base])
                  (generate-fn generated-values)
                  overrides))))

(defmacro deffactory [factory-name base & more]
  (let [config (apply hash-map more)]
    `(let [generators# (extract-generators ~config)
           factory-config# (merge (select-keys ~config [:extends-factory :generate])
                               {:generators generators# :base ~base})
           factory# (Factory. factory-config#)]
       (defmethod get-factory ~factory-name [_#]
         factory#))))
