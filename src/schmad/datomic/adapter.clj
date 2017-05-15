(ns schmad.datomic.adapter
  (:require [clojure.spec.alpha :as s]
            [datomic.api :as d :refer [tempid]]))
      
 
(def dat-types
  {:string    "string"
   :bool      "boolean"
   :boolean   "boolean"
   :keyword   "keyword"
   :long      "long"
   :bigint    "bigint"
   :float     "float"
   :double    "double"
   :bigdec    "bigdec"
   :ref       "ref"
   :instant   "instant"
   :uuid      "uuid"
   :uri       "uri"
   :byte      "byte"})

(defmulti ->type class)

(defmethod ->type java.lang.String [s]
  (-> s keyword dat-types)) 

(defmethod ->type :default [k]
  (-> k dat-types)) 
 
(defn uniqueness-check
  "Assoc Datomic uniqueness if it exists"
  [result uniq-attr]
  (if uniq-attr
    (assoc result :db/unique (keyword "db.unique" uniq-attr))
    result))

(defn datomic-schema-attribute [ent [ident {:keys [type cardinality unique index doc fulltext is-component no-history]
                                            :or   {index true doc "No docs provided" fulltext false unique nil 
                                                   cardinality "one" is-component false no-history false}}]]
  (let [ns-ident (keyword (name ent) (name ident))
        attr-map
        {:db/id                 (d/tempid :db.part/db)
         :db/ident              ns-ident
         :db/valueType          (keyword "db.type" (->type type))
         :db/cardinality        (keyword "db.cardinality" cardinality)
         :db/doc                doc
         :db/index              index
         :db/fulltext           fulltext
         :db/isComponent        is-component
         :db/noHistory          no-history
         :db.install/_attribute :db.part/db}]
    (uniqueness-check attr-map unique)))

(defn build-entity-sch
  "Creates Datomic schema for a single entity"
  [ent m]
  (map #(datomic-schema-attribute ent %) m))

(defn build-datomic-schema
  "Builds Datomic schema for from all entities as defined in data-schema"
  [result [ent ident-map]]
  (->> (build-entity-sch ent ident-map)
       (lazy-cat result)))

(s/def ::dat-record
  (s/keys :req [::type ::cardinality]
          :opt [::index ::doc ::fulltext ::unique ::is-component ::no-history]))

(s/def ::datomic 
  (s/map-of ::dat-record #(% true)))

(defn generate-schema 
  "Reduces on the build-datomic-schema func. Requires schema map to execute.
  Returns a datomic-transaction-able seq of datomic schema attrs.
  Takes a map with entity name as key and map of ident configuration values
  Requires  1) Ent name - e.g. :user
            2) Map with ident key - e.g. :email
            3) Value map with required keys (string values)- :type, :cardinality (one or many)
            4) Optional datomic attr keys include index, doc, fulltext, unique, is-component, no-history

  Example -  

  {::user    {::first_name  {::type ::string ::cardinality 'one'}
             ::last_name   {::type ::string ::cardinality 'one'}}
   ::company {::name        {::type ::string ::cardinality 'one' 
                           ::index true  ::doc 'Name of the company' 
                           ::fulltext true ::is-componene false 
                           ::unique true   ::no-history false}
             ::locations   {::type ::string ::cardinality 'many'}}}}
  "
  [m] 
    (if (s/valid? ::datomic m)
        (reduce build-datomic-schema [] m)
        (s/explain ::datomic m)))
