(ns schmad.adapter
  (:require [clojure.string :refer [capitalize]]
            [schmad.lacinia.adapter :as lacinia]
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

(defn postgresql [m]
  ;; TODO- ADD SPEC VALIDATION
  (->schema (->Postgresql m)))

(defn lacinia [m]
  ;; TODO- ADD SPEC VALIDATION
  (->schema (->Lacinia m)))

(defn datomic [m]
  ;; TODO- ADD SPEC VALIDATION
  (->schema (->Datomic m)))

(defn get-schema [record-type m]
   ((resolve (symbol (name record-type))) m))
