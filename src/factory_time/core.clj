(ns factory-time.core)

(defmulti build (fn [& args] (first args)))

(defn merge-or-replace [a b]
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

(defn build-generated-values [generators]
  (loop [generators generators
         acc {}]
    (if (empty? generators)
      acc
      (let [{:keys [name counter map-fn]} (first generators)
            current-count (swap! counter inc)
            generated-value (map-fn current-count)]
        (recur (rest generators) (assoc acc name generated-value))))))

(defmacro deffactory [factory-name base & more]
  (let [config (apply hash-map more)
        generators-sym (gensym "generators")]
    `(do
       (def ~generators-sym (extract-generators ~config))
       (defmethod build ~factory-name
         ([_#] (build ~factory-name {}))
         ([_# overrides#]
          (let [parent-builder# (if (contains? ~config :extends-factory)
                                  (partial build (:extends-factory ~config))
                                  (fn [] {}))
                generate-fn# (get ~config :generate identity)
                generated-values# (build-generated-values ~generators-sym)]
            (merge-with merge-or-replace (parent-builder#) ~base (generate-fn# generated-values#) overrides#)))))))
