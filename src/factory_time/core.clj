(ns factory-time.core)

(defmulti build (fn [& args] (first args)))

(defmacro deffactory [factory-name base]
  `(defmethod build ~factory-name 
     ([_#] (build ~factory-name {}))
     ([_# overrides#] (merge ~base overrides#))))
