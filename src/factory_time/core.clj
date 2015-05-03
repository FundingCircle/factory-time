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
    (if (contains? (:config this) :create!)
      (let [create-fn (get-in this [:config :create!])
            build-result (.build-obj this overrides)]
        (create-fn build-result))
      (throw (Exception. "No create! function provided")))))

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

(defmacro with-ids [seqable id-vars & body]
  "Generate named identities

   Intended to help link together related entities. For example, to associate multiple
   book entities with their author, one could use:

   ```
   (with-ids [[1] author]
     (build :author {:id author :name \"Irvine Welsh\"})
     (build :book {:author author :name \"Trainspotting\"})
     (build :book {:author author :name \"Acid House\"}))
   ```

   This makes it easier to specify groups of related entities to be used together in
   fixtures."
  `(let ~(into [] (apply concat (map vector id-vars seqable)))
     ~@body))
