(ns factory-time.core)

(defmulti build (fn [& args] (first args)))

(defn merge-or-replace [a b]
  (if (every? map? [a b])
    (merge a b)
    b))

(defmacro deffactory [factory-name base]
  `(defmethod build ~factory-name
     ([_#] (build ~factory-name {}))
     ([_# overrides#] (merge-with merge-or-replace ~base overrides#))))
