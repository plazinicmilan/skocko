(ns skocko.database.dbbroker
  (:require
    [monger.core :as mg]
    [monger.collection :as mc]
    [monger.conversion :refer :all]
    [monger.operators :refer :all]
    [clj-time.core :as t]
    [monger.joda-time :refer :all]
    [clojure.java.io]
    )
  )

(defn load-props
  [file-name]
  (with-open [^java.io.Reader reader (clojure.java.io/reader file-name)]
    (let [props (java.util.Properties.)]
      (.load props reader)
      (into {} (for [[k v] props] [(keyword k) (read-string v)])))))

(def property-map (load-props "skocko.properties"))

(def db (mg/get-db (mg/connect {:host (get property-map :host) :port (get property-map :port)})  (get property-map :db)))
(def table "games")

(defn insert-sign [dok sign]
  (try
    (if (or (nil? sign) (empty? sign)) "" (mc/update-by-id db "games" (get dok :_id) {$push {:signs sign}} ))
    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom inserta znaka!" e))))
  )

(defn create-new-doc []
  (try
    (mc/insert-and-return db table {})

    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom kreiranje novog dokumenta!" e))))
  )

(defn find-doc-by-id [dok]
  (try
    (mc/find-by-id db table (get dok :_id))
    (catch Exception e
      (try
        (mc/find-by-id db table (get dok "_id"))
        (catch Exception e
          (throw (Exception. "Dogodila se greska prilikom pronalazenja dokumenta po id-ju!" e))
          ))))
  )

(defn find-doc-by-full-id [id]
  (try
    (mc/find-by-id db table (to-object-id id))
    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom pronalazenja dokumenta po id-ju!" e))))
  )

(defn remove-sign [dok sign]
  (try
    (def doc (find-doc-by-id dok))
    (if (or (nil? sign) (empty? sign)) "" (mc/update-by-id db table (get dok :_id) {:signs (drop-last (get doc "signs"))} ))
    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom brisanja znaka!" e))))
  )

(defn insert-game-data [dok name points combination]
  (try
    (def doc (find-doc-by-id dok))
    (mc/update-by-id db table (get dok :_id) {:name name :points points :signs (get doc "signs") :date (t/from-time-zone (t/now)
                                                                                                                         (t/time-zone-for-offset -2)) :combination combination}  )
    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom inserta podataka o igri!" e))))
  )

(defn get-all-documents []
  (try
    (mc/find-maps db table)
    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom dohvatanja svih igara!" e))))
  )

(defn delete-game [id]
  (try
    (mc/remove-by-id db table (to-object-id id))
    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom brisanja podataka o igri!" e))))
  )

(defn delete-game-full-id [dok]
  (try
    (mc/remove-by-id db table (get dok :_id))
    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom brisanja podataka o igri!" e))))
  )