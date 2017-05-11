(ns schmad.datomic.adapter-test
  (:require [clojure.test :refer :all]
            [clojure.set :refer [difference]]
            [schmad.datomic.adapter :refer :all]))

(def test-default-schema-attrs
  {:db/doc                "No docs provided"
   :db/index              true
   :db/fulltext           false
   :db/isComponent        false
   :db/noHistory          false
   :db.install/_attribute :db.part/db})

(deftest builder-test-uniqueness-check-unique-attr-nil
  (testing "A nil unique attribute should simply return the given map"
    (let [original-map {:test 1}
          updated-map (uniqueness-check original-map nil)]
      (is (= updated-map original-map)))))

(deftest builder-test-uniqueness-check-unique-attr-identity
  (testing "A unique idenity attribute should return a map with attribute key-value assoc-ed"
    (let [original-map {:test 1}
          updated-map (uniqueness-check original-map "identity")
          expected-result :db.unique/identity]
      (is (= (:db/unique updated-map) expected-result)))))

(deftest builder-test-uniqueness-check-unique-attr-value
  (testing "A unique value attribute should return a map with attribute key-value assoc-ed"
    (let [original-map {:test 1}
          updated-map (uniqueness-check original-map "value")
          expected-result :db.unique/value]
      (is (= (:db/unique updated-map) expected-result)))))

(deftest builder-test-datomic-schema-attribute-defaults
  (testing "Setting datomic-schema-attribute map with only required keys correctly sets
  default attributes in Datomic-approved db namespaced key-values."
    (let [default-schema-attrs test-default-schema-attrs
          example-ident [:test {:type "string" :cardinality "many"}]
          example-ns :example
          result (datomic-schema-attribute example-ns example-ident)
          selected (select-keys result [:db/doc :db/index :db/fulltext :db/isComponent :db/noHistory :db.install/_attribute])]
      (is (= selected default-schema-attrs)))))

(deftest builder-test-datomic-schema-attribute-correctly-assocs-given-attrs
  (testing "Correctly assocs given attrs to a result map"
    (let [doc-string {:db/doc "An Example of ident documentation"}
          fulltext true
          is-component true
          index false
          no-history true
          type [:db.type/string "string"]
          cardinality [:db.cardinality/many "many"]
          unique [:db.unique/identity "identity"]
          example-ident
          [:test {:type  (last type) :cardinality (last cardinality) :doc doc-string :fulltext fulltext
                  :index index :is-component is-component :no-history no-history :unique (last unique)}]
          example-ns :example
          result (datomic-schema-attribute example-ns example-ident)]
      (is (= (:db/doc result) doc-string))
      (is (= (:db/fulltext result) fulltext))
      (is (= (:db/isComponent result) is-component))
      (is (= (:db/index result) index))
      (is (= (:db/noHistory result) no-history))
      (is (= (:db/valueType result) (first type)))
      (is (= (:db/cardinality result) (first cardinality)))
      (is (= (:db/unique result) (first unique)))
      (is (= (:db/ident result) :example/test)))))

(deftest builder-test-build-entity-sch-seq-datomic-maps
  (testing "Returns a seq of datomic schema maps"
    (let [ent :auth
          idents {:password
                  {:type        "string"
                   :cardinality "one"}
                  :username
                  {:type        "string"
                   :cardinality "one"}}
          result (build-entity-sch ent idents)
          datomic-key-set (set (conj (keys test-default-schema-attrs) :db/valueType :db/cardinality))
          result-set (into #{} (keys (first result)))]
      (is (= (count idents) (count result)))
      (is (= true (empty? (difference datomic-key-set result-set)))))))

(run-tests *ns*)
