(ns datomic-schema-adapter.util
  (:require [datomic.api :as d :refer [temp-id]]
            [clojure.set :as set :refer [rename-keys]]))


(defn dat-types
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

