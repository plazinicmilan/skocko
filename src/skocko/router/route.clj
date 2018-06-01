(ns skocko.router.route
  (:use
    [hiccup.core :refer :all]
    [hiccup.page :refer :all]
    [compojure.core :refer [defroutes GET]]
    [compojure.route :refer [not-found]]
    [ring.handler.dump :refer [handle-dump]]
    )
  (:require
    [skocko.database.dbbroker :as db]))


(def combination)

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

(def sign-list ["+" "-" "*" "$" "@" "!"])

(defn random-table [choose-list] [(rand-nth choose-list)
                                  (rand-nth choose-list)
                                  (rand-nth choose-list)
                                  (rand-nth choose-list)] )

(defn check-if-fourth [dokum]
  (if (.contains [1 2 3 4 5 6] (/ (count (get dokum "signs")) 4)) true false))

(def choose-table (html  [:table {:id "choose-table" :style "border: 1px solid #000000"}
                          [:tr
                           [:td [:form {:id "addPlus" :action "/newGame/+"} [:input {:id "plus" :type "submit" :value "+"}] ]]
                           [:td [:form {:id "addMinus" :action "/newGame/-"} [:input {:id "minus":type "submit" :value "-"}] ]]
                           [:td [:form {:id "addStar" :action "/newGame/*"} [:input {:id "star" :type "submit" :value "*"}] ]]
                           [:td [:form {:id "addDol" :action "/newGame/$"} [:input {:id "dot" :type "submit" :value "$"}] ]]
                           [:td [:form {:id "addMonkey" :action "/newGame/@"} [:input {:id "monkey" :type "submit" :value "@"}] ]]
                           [:td [:form {:id "addHash" :action "/newGame/!"} [:input {:id "hash" :type "submit" :value "!"}]]]]]
                         [:table {:id "remove-element" :style "border: 1px solid #000000"}
                          [:tr
                           [:td [:form {:id "removeSign" :action "/newGame/remove"} [:input {:id "remove" :type "submit" :value "X"}]]]]]))

(defn table [dok] (try
                   (html [:table {:id "tableskocko" :style "border: 1px solid #000000"}
                           [:tr {:id "tr1"}
                            [:td (nth (get dok "signs") 0 "")]
                            [:td (nth (get dok "signs") 1 "")]
                            [:td (nth (get dok "signs") 2 "")]
                            [:td (nth (get dok "signs") 3 "")]]
                           [:tr
                            [:td (nth (get dok "signs") 4 "")]
                            [:td (nth (get dok "signs") 5 "")]
                            [:td (nth (get dok "signs") 6 "")]
                            [:td (nth (get dok "signs") 7 "")]]
                           [:tr
                            [:td (nth (get dok "signs") 8 "")]
                            [:td (nth (get dok "signs") 9 "")]
                            [:td (nth (get dok "signs") 10 "")]
                            [:td (nth (get dok "signs") 11 "")]]
                           [:tr
                            [:td (nth (get dok "signs") 12 "")]
                            [:td (nth (get dok "signs") 13 "")]
                            [:td (nth (get dok "signs") 14 "")]
                            [:td (nth (get dok "signs") 15 "")]]
                           [:tr
                            [:td (nth (get dok "signs") 16 "")]
                            [:td (nth (get dok "signs") 17 "")]
                            [:td (nth (get dok "signs") 18 "")]
                            [:td (nth (get dok "signs") 19 "")]]
                           [:tr
                            [:td (nth (get dok "signs") 20 "")]
                            [:td (nth (get dok "signs") 21 "")]
                            [:td (nth (get dok "signs") 22 "")]
                            [:td (nth (get dok "signs") 23 "")]]
                           ]
                          )
                    (catch Exception e
                      (throw (Exception. (str "Dogodila se greska prilikom crtanja tabele! " e))))))

(defn correct-answer-table [dokument] (try
                    (html [:table {:id "tableskockocheck" :style "border: 1px solid #000000"}

                           (if (and (not (nil? (get dokument "signs")))
                                    (not (empty? (nth (get dokument "signs") 0 "")))
                                    (not (empty? (nth (get dokument "signs") 1 "")))
                                    (not (empty? (nth (get dokument "signs") 2 "")))
                                    (not (empty? (nth (get dokument "signs") 3 ""))))
                             (do (def comb1 (check-combination (nth (get dokument "signs") 0)
                                                              (nth (get dokument "signs") 1)
                                                              (nth (get dokument "signs") 2)
                                                              (nth (get dokument "signs") 3)))
                                 [:tr
                                  [:td (nth comb1 0)]
                                  [:td (nth comb1 1)]
                                  [:td (nth comb1 2)]
                                  [:td (nth comb1 3)]])
                             [:tr
                              [:td ""]
                              [:td ""]
                              [:td ""]
                              [:td ""]])

                           (if (and (not (nil? (get dokument "signs")))
                                    (not (empty? (nth (get dokument "signs") 4 "")))
                                    (not (empty? (nth (get dokument "signs") 5 "")))
                                    (not (empty? (nth (get dokument "signs") 6 "")))
                                    (not (empty? (nth (get dokument "signs") 7 ""))))
                             (do (def comb2 (check-combination (nth (get dokument "signs") 4)
                                                              (nth (get dokument "signs") 5)
                                                              (nth (get dokument "signs") 6)
                                                              (nth (get dokument "signs") 7)))
                                 [:tr
                                  [:td (nth comb2 0)]
                                  [:td (nth comb2 1)]
                                  [:td (nth comb2 2)]
                                  [:td (nth comb2 3)]])
                             [:tr
                              [:td ""]
                              [:td ""]
                              [:td ""]
                              [:td ""]])

                           (if (and (not (nil? (get dokument "signs")))
                                    (not (empty? (nth (get dokument "signs") 8 "")))
                                    (not (empty? (nth (get dokument "signs") 9 "")))
                                    (not (empty? (nth (get dokument "signs") 10 "")))
                                    (not (empty? (nth (get dokument "signs") 11 ""))))
                             (do (def comb3 (check-combination (nth (get dokument "signs") 8)
                                                              (nth (get dokument "signs") 9)
                                                              (nth (get dokument "signs") 10)
                                                              (nth (get dokument "signs") 11)))
                                 [:tr
                                  [:td (nth comb3 0)]
                                  [:td (nth comb3 1)]
                                  [:td (nth comb3 2)]
                                  [:td (nth comb3 3)]])
                             [:tr
                              [:td ""]
                              [:td ""]
                              [:td ""]
                              [:td ""]])

                           (if (and (not (nil? (get dokument "signs")))
                                    (not (empty? (nth (get dokument "signs") 12 "")))
                                    (not (empty? (nth (get dokument "signs") 13 "")))
                                    (not (empty? (nth (get dokument "signs") 14 "")))
                                    (not (empty? (nth (get dokument "signs") 15 ""))))
                             (do
                               (def comb4 (check-combination (nth (get dokument "signs") 12)
                                                             (nth (get dokument "signs") 13)
                                                             (nth (get dokument "signs") 14)
                                                             (nth (get dokument "signs") 15)))
                                 [:tr
                                  [:td (nth comb4 0)]
                                  [:td (nth comb4 1)]
                                  [:td (nth comb4 2)]
                                  [:td (nth comb4 3)]])
                             [:tr
                              [:td ""]
                              [:td ""]
                              [:td ""]
                              [:td ""]])

                           (if (and (not (nil? (get dokument "signs")))
                                    (not (empty? (nth (get dokument "signs") 16 "")))
                                    (not (empty? (nth (get dokument "signs") 17 "")))
                                    (not (empty? (nth (get dokument "signs") 18 "")))
                                    (not (empty? (nth (get dokument "signs") 19 ""))))
                             (do
                               (def comb5 (check-combination (nth (get dokument "signs") 16)
                                                             (nth (get dokument "signs") 17)
                                                             (nth (get dokument "signs") 18)
                                                             (nth (get dokument "signs") 19)))

                               [:tr
                                [:td (nth comb5 0)]
                                [:td (nth comb5 1)]
                                [:td (nth comb5 2)]
                                [:td (nth comb5 3)]])
                             [:tr
                              [:td ""]
                              [:td ""]
                              [:td ""]
                              [:td ""]])

                           (if (and (not (nil? (get dokument "signs")))
                                    (not (empty? (nth (get dokument "signs") 20 "")))
                                    (not (empty? (nth (get dokument "signs") 21 "")))
                                    (not (empty? (nth (get dokument "signs") 22 "")))
                                    (not (empty? (nth (get dokument "signs") 23 ""))))
                             (do (def comb6 (check-combination (nth (get dokument "signs") 20)
                                                              (nth (get dokument "signs") 21)
                                                              (nth (get dokument "signs") 22)
                                                              (nth (get dokument "signs") 23)))
                                 [:tr
                                  [:td (nth comb6 0)]
                                  [:td (nth comb6 1)]
                                  [:td (nth comb6 2)]
                                  [:td (nth comb6 3)]])
                             [:tr
                              [:td ""]
                              [:td ""]
                              [:td ""]
                              [:td ""]])
                           ]
                          )
                    (catch Exception e
                      (throw (Exception. (str "Dogodila se greska prilikom crtanja tabele! " e))))))

(defn welcome
  "A ring handler to process all requests sent to the webapp"
  [request]
  {:status 200
   :body (html [:h1 {:style "color: green"} "Welcome to Skocko"]
               [:form {:id "newGameForm" :action "/newGame"} [:input {:type "submit" :value "New game"}] ])

   :headers {}})



(defn hello
  "A simple personalised greeting showing the use of variable path elements"
  [request]
  (let [name (get-in request [:params :name]) surname (get-in request [:params :surname])]
    {:status 200
     :body (str "Hello " name ".  I got your name from the web URL. and your sur: " surname)
     :headers {}}))



(defn update-sign [dok sign]
  (try
    (def doc (db/find-doc-by-id dok))
    (if (= sign "remove") (if (not (check-if-fourth doc))
                            (do  (def doc (db/find-doc-by-id dok))
                                 (db/remove-sign dok (last (get doc "signs")))))
                          (db/insert-sign dok sign))
    (def doc (db/find-doc-by-id dok))
    (if (check-if-fourth doc) (do (def answer-table (correct-answer-table doc))
                                  answer-table))
    (str (table doc) choose-table combination answer-table)
    (catch Exception e
      (throw (Exception. e))))
  )

(defroutes route-defs
  (GET "/" [] welcome)
  (GET "/newGame" [] (try
                       (println "SVeE normaljna")
                       (def dok (db/create-new-doc) )
                       (def combination (random-table sign-list))
                       (def answer-table (correct-answer-table dok))
                       (str (table "") choose-table combination answer-table)
                       (catch Exception e)))
  (GET "/newGame/:sign" [sign] (try
                                (update-sign dok sign)
                                (catch Exception e)))

  (GET "/hello/:name&:surname" [] hello)
  (not-found "<h1>This is not the page you are looking for</h1>
              <p>Sorry, the page you requested was not found!</p>")
           )

