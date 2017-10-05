(ns schmad.postgresql.adapter 
  (:require [clojure.string :as string :refer [lower-case replace split join]]))

(declare convert-hyphen-to-underscore)

(def ^:private POSTGRES "postgresql")
(def ^:private hyphen-warning "Please be aware that your hyphens will be converted to underscores. If you want hyphens in your migration, you will need to update them manually.")
(def ^:private unsupported-migration-action "The requested migration action is misspelled or unsupported at this time for action:")
(def ^:private empty-migration "There was a problem creating the sql string. Generating empty migration file.")
(def ^:private malformed-column "There is a problem with the column. Omitting from the migration: ")

(def ^:private create-table-str "CREATE TABLE IF NOT EXISTS")
(def ^:private drop-table-str "DROP TABLE IF EXISTS")
(def ^:private alter-table-str "ALTER TABLE")
(def ^:private add-column-str "ADD COLUMN")
(def ^:private drop-column-str "DROP COLUMN")
(def ^:private rename-column-str "RENAME COLUMN")
(def ^:private cascade-str "CASCADE")
(def ^:private not-null "NOT NULL")
(def ^:private blank-space " ")
(def ^:private comma ",")
(def ^:private open-paren "(")
(def ^:private closed-paren ")")
(def ^:private semi-colon ";")
(def ^:private cli-delimiter ":")
(def ^:private underscore "_")
(def ^:private newline-delimiter (System/lineSeparator))
(def ^:private tab-delimiter "\t")
(def ^:private tabbed-newline (str newline-delimiter tab-delimiter))
(def ^:private hyphen "-")
(def ^:private pg-types {:path "path" :money "money" :bytea "bytea" :date "date" :pg_lsn "pg_lsn" :bigserial "bigserial" 
                         :cidr "cidr" :time "time" :numeric "numeric" :bigint "bigint" :macaddr "macaddr" :jsonb "jsonb" 
                         :tsquery "tsquery" :txid_snapshot "txid_snapshot" :box "box" :circle "circle" :inet "inet" 
                         :smallserial "smallserial" :serial "serial" :varchar "varchar" 
                         :timestamp "timestamp" :realized? "realized?" :point "point" :bit "bit" :interval "interval"                                 
                         :double_precision "double precision" :uuid "uuid" :line-seq "line-seq" :json "json" :tsvector "tsvector" 
                         :xml "xml" :lseg "lseg" :boolean "boolean" :character "character" :smallint "smallint" :polygon "polygon" :text "text"
                         :string "text"})

(defn- space-delimited-str [params] (join blank-space params))

(defn- convert-hyphen-to-underscore [str-param]
  (let [hyphen-pattern (re-pattern hyphen)]
    (string/replace str-param hyphen-pattern underscore)))

(defn set-column [[fieldname {:keys [type required? sql-params]}]] 
  (cond->     (name fieldname)
   type       (str blank-space (get pg-types type type))
   sql-params (str blank-space (join blank-space sql-params))
   required?  (str blank-space not-null)))

(defn create-table [tablename columns]
  (let [create-tbl-line (space-delimited-str [create-table-str (str (name tablename) open-paren)])
        column-attrs (join comma (map #(str tabbed-newline %) (map set-column columns)))
        closing (str closed-paren semi-colon)]
      [(str create-tbl-line column-attrs) closing]))

(defn drop-table [tablename columns]
  [(space-delimited-str [drop-table-str tablename cascade-str])])

(defn add-column [tablename columns]
  (let [prepended-with-add-column (map #(space-delimited-str (vector add-column-str %)) columns)
        formatted-columns (str (set-column prepended-with-add-column) semi-colon)]
    [(space-delimited-str [alter-table-str tablename formatted-columns])]))

(defn drop-column [tablename columns] [(space-delimited-str [alter-table-str tablename drop-column-str])])

(defn update-column [tablename columns] [(space-delimited-str [alter-table-str tablename add-column-str])])

(defn build-schema-for-table 
  "Returns a SQL string for a database migration file. Logs to console and returns nil if
   input config errors."
  [[tablename columns]]
  (let [schema (create-table tablename columns)]
    (join newline-delimiter schema)))

;; PUBLIC API ;;
(defn generate-schema [m]
  (map build-schema-for-table m))

(defn update-schema [table-action & columns])
  
(defn supported-migration-docs
  ([] (supported-migration-docs 'schmad.postgresql.adapter))
  ([ns] (#(list (ns-name %) (map first (ns-publics %))) ns)))
