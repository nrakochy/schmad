(def user
  {:user
   {:email
    {:type           "string"
     :cardinality    "one"
     :unique         "identity"
     :doc            "Reference-able unique email for a user"
     :fulltext       true
     :user-editable? true
     :optional       false}
    :first-name
    {:type           "string"
     :cardinality    "one"
     :doc            "User's first name"
     :index          false
     :user-editable? true
     :optional       false}
    :last-name
    {:type           "string"
     :cardinality    "one"
     :doc            "User's last name"
     :user-editable? true
     :optional       false}
    :permissions
    {:type           "ref"
     :ref-ent        [:permission "group"]
     :cardinality    "many"
     :doc            "Permission groups of which user is a part"
     :user-editable? false
     :optional       false}
    :preferences
    {:type           "string"
     :is-component   true
     :cardinality    "many"
     :doc            "Component preferences of a given user"
     :user-editable? true
     :optional       true}}})

(def permission
  {:permission
   {:group
    {:type           "string"
     :cardinality    "one"
     :unique         "identity"
     :doc            "Reference-able unique name for a permission group"
     :user-editable? false
     :optional       false}}})

(def auth
  {:auth
   {:password
    {:type           "string"
     :cardinality    "one"
     :doc            "Password"
     :user-editable? true
     :optional       false}
    :username
    {:type           "string"
     :cardinality    "one"
     :unique         "attribute"
     :doc            "Unique username"
     :user-editable? true
     :optional       false}}})

(def schema (merge user permission auth))
