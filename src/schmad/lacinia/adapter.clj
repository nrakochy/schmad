(ns schmad.lacinia.adapter
  (:require [clojure.string :refer [capitalize]]))

(defmulti ->sym "Converts given value to a Symbol based on type. Defaults to key" {:arglists '([arg])}  class) 

(defmethod ->sym java.lang.String [s] 
  (symbol (capitalize s)))

(defmethod ->sym :default [k]
  (symbol (capitalize (name k))))

(defn set-list 
  "Creates an unresolved function at runtime to match the Lacinia API"
  [k] 
  `(~(symbol "list") ~(keyword (namespace k))))

(defmulti get-field-config (fn [[_ m]] (:type m)))

(defmethod get-field-config :ref [[_ {:keys [ref-ent public?]}]]
  (if public?
    [:appears_in {:type (set-list ref-ent)}]))
    
(defmethod get-field-config :default
  [[fieldname
     {:as   config
      :keys [doc type cardinality public?]
      :or   {public? true cardinality :one 
             doc "No description"}}]]
  (if public?
    (let [sch {:description doc
               :type (->sym type)}]
           [fieldname sch])))

(defn get-entities [results [entity-name fields]]
  (let [fields {:fields (into {} (map get-field-config fields))}]
    (assoc results entity-name fields)))

(defn generate-schema 
  "Returns a map of Lacinia-formatted data configuration"  
  [m]
  {:objects (reduce get-entities {} m)})
