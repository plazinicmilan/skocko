(ns skocko.database.dbbroker
  (:require
    [monger.core :as mg]
    [monger.collection :as mc]
    [monger.conversion :refer :all]
    [monger.operators :refer :all]
    )
  )

(def db (mg/get-db (mg/connect {:host "172.18.0.2" :port 27017})  "skocko"))

(defn insert-sign [dok sign]
  (try
    (if (or (nil? sign) (empty? sign)) "" (mc/update-by-id db "games" (get dok :_id) {$push {:signs sign}} ))
    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom inserta znaka!"))))
  )

(defn create-new-doc []
  (try
    (mc/insert-and-return db "games" {})

    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom kreiranje novog dokumenta!"))))
  )

(defn find-doc-by-id [dok]
  (try
    (mc/find-by-id db "games" (get dok :_id))
    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom pronalazenja dokumenta po id-ju!"))))
  )

(defn remove-sign [dok sign]
  (try
    (def doc (find-doc-by-id dok))
    (if (or (nil? sign) (empty? sign)) "" (mc/update-by-id db "games" (get dok :_id) {:signs (drop-last (get doc "signs"))} ))
    (catch Exception e
      (throw (Exception. "Dogodila se greska prilikom inserta znaka!"))))
  )