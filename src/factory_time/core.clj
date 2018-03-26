(ns factory-time.core)

(defmulti get-factory identity)

(defn build
  ([factory-name] (build factory-name {}))
  ([factory-name overrides]
   (let [factory (get-factory factory-name)]
     (.build-obj factory overrides))))

(defn create!
  ([factory-name] (create! factory-name {}))
  ([factory-name overrides]
   (let [factory (get-factory factory-name)]
     (.create-obj! factory overrides))))

(defn- merge-or-replace [a b]
  (if (every? map? [a b])
    (merge a b)
    b))

(defn- build-generated-values [counter generators]
  (let [n (swap! counter inc)]
    (reduce (fn [acc [prop-name map-fn]]
              (merge acc {prop-name (map-fn n)}))
            {}
            generators)))

(defn- parent-factories [factory]
  (if-let [parent-factory-name (get-in factory [:config :extends-factory])]
    (let [parent-factory (get-factory parent-factory-name)]
      (cons parent-factory
            (lazy-seq (parent-factories parent-factory))))
    nil))

(defprotocol Buildable
  (build-obj [this overrides])
  (create-obj! [this overrides]))

(defrecord Factory [config]
  Buildable
  (build-obj [this overrides]
    (let [parent-builder (if (contains? (:config this) :extends-factory)
                           (partial build (get-in this [:config :extends-factory]))
                           (fn [] {}))
          generated-values (build-generated-values (get-in this [:config :counter])
                                                   (get-in this [:config :generators] {}))]
      (merge-with merge-or-replace
                  (parent-builder)
                  (get-in this [:config :base])
                  generated-values
                  overrides)))
  (create-obj! [this overrides]
    (let [create-fn (or (get-in this [:config :create!])
                        (->> (parent-factories this)
                             (map #(get-in % [:config :create!]))
                             (filter fn?)
                             (first))
                        (throw (Exception. "No create! function provided")))
          build-result (.build-obj this overrides)]
      (create-fn build-result))))

(defmacro deffactory [factory-name base & more]
  (let [config (apply hash-map more)]
    `(let [generators# (get ~config :generators {})
           counter# (atom 0)
           factory-config# (merge (select-keys ~config [:extends-factory :generators :create!])
                               {:base ~base
                                :counter counter#})
           factory# (Factory. factory-config#)]
       (defmethod get-factory ~factory-name [_#]
         factory#))))
