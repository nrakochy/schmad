(ns schmad.adapter
  (:require [schmad.lacinia.adapter :as lacinia]
            [schmad.postgresql.adapter :as postgresql]
            [schmad.datomic.adapter :as datomic]))

(defprotocol Schema
  "Generating type-specific schemas from a single generic map"
   (->schema [m]))

(defrecord Postgresql [m]
  Schema
   (->schema [_] (postgresql/generate-schema m)))

(defrecord Lacinia [m]
  Schema
   (->schema [_] (lacinia/generate-schema m)))

(defrecord Datomic [m]
  Schema
   (->schema [_] (datomic/generate-schema m)))

(defn schema-constructor [k]
 (fn [m]
   (resolve (symbol (str "->" (name k)))) m))

(defn get-schema [record-type m]
   ((schema-constructor record-type) m))
