(ns schmad.adapter
 (:require  [clojure.string :as string :refer [capitalize]]
            [schmad.lacinia.adapter :as lacinia]
            [schmad.postgresql.adapter :as postgresql]
            [schmad.datomic.adapter :as datomic]))

(defprotocol Schema
  "Generating type-specific schemas from a single generic map"
   (->schema [schema]))

(defrecord Postgresql [schema]
  Schema
   (->schema [_] (postgresql/generate-schema schema)))

(defrecord Lacinia [schema]
  Schema
   (->schema [_] (lacinia/generate-schema schema)))

(defrecord Datomic [schema]
  Schema
   (->schema [_] (datomic/generate-schema schema)))

(defrecord Default [schema]
  Schema
   (->schema [_] schema))

(defn schema-constructor
 "Converts given key to Clojure's OOTB record constructor (e.g. ->RecordName) 
  which is generated with defrecord. Returns symbol"
  [k]
  (resolve (symbol (str "->" (string/capitalize (name k))))))

(defn get-schema [record-type m]
   (->schema ((schema-constructor record-type) m)))
