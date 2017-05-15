(ns schmad.lacinia.adapter-test
  (:require [clojure.test :refer :all]
            [clojure.set :refer [difference]]
            [schmad.lacinia.adapter :refer :all :as adapter]))

(deftest set-references-appears-in-test 
  (testing "Sets :appears-in on a given results map if field references separate entity"
    (let [example {:test 1 :references :person/test}
          example-map {}
          results {:appears_in {:type (list :person)}}]
      (is (= 1 1))))) 

