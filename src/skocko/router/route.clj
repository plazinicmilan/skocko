(ns skocko.router.route
  (:use
    [hiccup.core :refer :all]
    [hiccup.page :refer :all]
    [hiccup.def :refer :all]
    [hiccup.util :refer :all]
    [hiccup.bootstrap.page :refer :all]
    [hiccup.bootstrap.element :refer :all]
    [compojure.core :refer [defroutes GET POST]]
    [compojure.route :refer [not-found]]
    [ring.handler.dump :refer [handle-dump]]
    [ring.util.response :refer [redirect]]
    [clj-time.format :refer :all]
    )
  (:require
    [skocko.database.dbbroker :as db]
    [mikera.image.filters :refer :all]
    [skocko.pages.preview :as page]
    [skocko.pages.forms :as forma]
    ))

(def sign-list ["+" "-" "*" "$" "@" "!"])
(def combination)
(def finished false)
(def points)
(def preview false)
(def save false)
(def answer-table)
(def dok nil)
(def result)
(def temp-combination)
(def temp-combination-half)

(defn random-table [choose-list] [(rand-nth choose-list)
                                  (rand-nth choose-list)
                                  (rand-nth choose-list)
                                  (rand-nth choose-list)] )

(defn check-if-fourth [dokum]
  (integer? (/ (count (get dokum "signs")) 4)))

(defn check-combination [x0 x1 x2 x3]
  (def result ["" "" "" ""])
  (def temp-combination combination)
  (if (= x0 (nth temp-combination 0)) (do (def result (assoc result (.indexOf result "") "c"))
                                          (def temp-combination (assoc temp-combination 0 1))) )

  (if (= x1 (nth temp-combination 1)) (do (def result (assoc result (.indexOf result "") "c"))
                                          (def temp-combination (assoc temp-combination 1 1))))

  (if (= x2 (nth temp-combination 2)) (do (def result (assoc result (.indexOf result "") "c"))
                                          (def temp-combination (assoc temp-combination 2 1))))

  (if (= x3 (nth temp-combination 3)) (do (def result (assoc result (.indexOf result "") "c"))
                                          (def temp-combination (assoc temp-combination 3 1))))

  (def temp-combination-half temp-combination)
  (if (and (not (= (nth temp-combination-half 0) 1)) (.contains temp-combination x0))
    (do (def result (assoc result (.indexOf result "") "z"))
        (def temp-combination (assoc temp-combination (.indexOf temp-combination x0) 1))))

  (if (and (not (= (nth temp-combination-half 1) 1)) (.contains temp-combination x1))
    (do (def result (assoc result (.indexOf result "") "z"))
        (def temp-combination (assoc temp-combination (.indexOf temp-combination x1) 1))))

  (if (and (not (= (nth temp-combination-half 2) 1)) (.contains temp-combination x2))
    (do (def result (assoc result (.indexOf result "") "z"))
        (def temp-combination (assoc temp-combination (.indexOf temp-combination x2) 1))))

  (if (and (not (= (nth temp-combination-half 3) 1)) (.contains temp-combination x3))
    (do (def result (assoc result (.indexOf result "") "z"))
        (def temp-combination (assoc temp-combination (.indexOf temp-combination x3) 1))))

  result
  )

(defn calculate-points [first]
  (do (def finished true)
      (case first
        0 (def points 35)
        4 (def points 30)
        8 (def points 25)
        12 (def points 20)
        16 (def points 15)
        20 (def points 10)
        (def points 0))))

(defn check-full-combination [dokument rb]
  (let [first-element (* 4 rb) last-element (+ first-element 3) range (range first-element (+ last-element 1))]
    (if (not (empty? (nth (get dokument "signs") last-element "")))
      (let [comb (check-combination (nth (get dokument "signs") (nth range 0))
                                    (nth (get dokument "signs") (nth range 1))
                                    (nth (get dokument "signs") (nth range 2))
                                    (nth (get dokument "signs") (nth range 3)))]
        (do (if (= comb ["c" "c" "c" "c"])
              (calculate-points first-element) (if (= 20 first-element) (calculate-points -1)))
            (forma/correct-table-row comb)))
      (forma/correct-table-row "")))
  )

(defn correct-answer-table [dokument] (try
                                       (html [:table {:id "tableskockocheck" :style "border: 1px solid #000000"}
                                               (for [x (range 0 6)]
                                                 (check-full-combination dokument x))])
                                        (catch Exception e
                                          (println e))))

(defn welcome []
  (try
    (if (and (not (nil? dok)) (not preview)) (db/delete-game-full-id dok))
    (def dok nil)
    (page/pageWelcome "Skočko"
                      (forma/welcome-form))
    (catch Exception e
      (throw e)))
  )

(defn update-sign [dok sign]
  (try

    (let [doc (db/find-doc-by-id dok)]
      (case sign
        "remove" (if (not (check-if-fourth doc))
                   (db/remove-sign dok (last (get doc "signs"))))
        (db/insert-sign dok sign))

      )
    (let [doc (db/find-doc-by-id dok)]
      (if (check-if-fourth doc) (do (def answer-table (correct-answer-table doc))
                                    answer-table)))

    (redirect "/newGame")
    (catch Exception e
      (throw (.getMessage e))))
  )

(defn save-game [request]
  (try
    (let [playername (clojure.string/replace (subs (get-in request [:query-string]) 11) "+" " ")]
      (if (or (nil? playername) (empty? (clojure.string/trim playername)))
        (def save true)
        (do (db/insert-game-data dok playername points combination)
            (def dok nil))))

    (redirect "/newGame")
    (catch Exception e
      (throw (.getMessage e))))
  )

(defn get-all-docs []
  (try
    (let [documents (db/get-all-documents)]
      (page/pageShowResults "Skočko - results"
                            (str (html [:div {:id "hp-btn-result"} (forma/home-page-button)] ) (forma/show-games documents))))
    (catch Exception e
      (throw (.getMessage e))))
  )

(defn delete-game [id]
  (try
    (db/delete-game id)
    (redirect "/results")
    (catch Exception e
      (throw e)))
  )

(defn view-game [id]
  (try
    (def dok (db/find-doc-by-full-id id))
    (def combination [(nth (get dok "combination") 0)
                      (nth (get dok "combination") 1)
                      (nth (get dok "combination") 2)
                      (nth (get dok "combination") 3)])
    (def answer-table (correct-answer-table dok))
    (def finished true)
    (def points (get dok "points"))
    (def preview true)
    (redirect "/newGame")

    (catch Exception e
      (throw (.getMessage e))))
  )

(defroutes route-defs
           (GET "/" [] (welcome))
           (GET "/newGame" [] (try
                                (if (nil? dok) (do (def dok (db/create-new-doc))
                                                   (def combination (random-table sign-list))
                                                   (def answer-table (correct-answer-table dok))
                                                   (def finished false)
                                                   (def preview false)
                                                   (def save false)
                                                   ))
                                (let [docc (db/find-doc-by-id dok)]
                                          (page/pageNewGame "Skočko - New game"
                                                            (str (forma/home-page-button) (if preview (forma/result-button))
                                                                 (html [:div {:id "main-table"}
                                                                        (forma/create-table docc)
                                                                        (if finished (forma/show-combination combination))
                                                                        ;(forma/show-combination combination)
                                                                        ]) answer-table
                                                                 (if (not finished) (forma/choose-table))
                                                                 (if (and finished (not preview)) (forma/congratulate-panel (get docc "name") points save))
                                                                 (if preview (forma/show-game-info docc)))))

                                (catch Exception e
                                  (throw (.getMessage e)))))

           (GET "/newGame/:sign" [sign] (try
                                          (update-sign dok sign)
                                          (catch Exception e
                                            (throw (.getMessage e)))))
           (GET "/savegame" [] (try
                                 save-game
                                 (catch Exception e
                                   (throw (.getMessage e)))))
           (GET "/results" [] (try
                                (get-all-docs)
                                (catch Exception e
                                  (throw (.getMessage e)))))

           (GET "/deleteGame/:id" [id] (try
                                         (delete-game id)
                                         (catch Exception e
                                           (throw (.getMessage e)))))
           (GET "/viewGame/:id" [id] (try
                                       (view-game id)
                                       (catch Exception e
                                         (throw (.getMessage e)))))
           (GET "/newGameWithoutSaving" [] (try
                                             (db/delete-game-full-id dok)
                                         (def dok nil)
                                         (redirect "/newGame")
                                       (catch Exception e
                                         (throw (.getMessage e)))))

           (not-found "<h1>This is not the page you are looking for</h1>
              <p>Sorry, the page you requested was not found!</p>")
           )

