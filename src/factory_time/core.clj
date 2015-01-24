(ns factory-time.core)

(defmulti build (fn [& args] (first args)))

(defn merge-or-replace [a b]
  (if (every? map? [a b])
    (merge a b)
    b))

(defmacro deffactory [factory-name base & more]
  (let [config (apply hash-map more)]
    `(defmethod build ~factory-name
       ([_#] (build ~factory-name {}))
       ([_# overrides#]
        (let [parent-builder# (if (contains? ~config :extends-factory)
                                (partial build (:extends-factory ~config))
                                (fn [] {}))]
          (merge-with merge-or-replace (parent-builder#) ~base overrides#))))))
