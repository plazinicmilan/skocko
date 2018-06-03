(ns skocko.router.route
  (:use
    [hiccup.core :refer :all]
    [hiccup.page :refer :all]
    [hiccup.def :refer :all]
    [hiccup.util :refer :all]
    [hiccup.bootstrap.page :refer :all]
    [hiccup.bootstrap.element :as hbe]
    [compojure.core :refer [defroutes GET POST]]
    [compojure.route :refer [not-found]]
    [ring.handler.dump :refer [handle-dump]]
    [ring.util.response :refer [redirect]]
    [clj-time.format :as f]
    )
  (:require
    [skocko.database.dbbroker :as db]
    [mikera.image.filters :as filt]
    [skocko.pages.preview :as page]))


(def combination)
(def finished false)
(def points)
(def dok nil)
(def preview false)
(def save false)
(def playername)

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

(def home-page-button (html  [:div {:id "homep"}
                              [:form {:id "home-page-form" :action "/"}
                               [:input {:id "home-page" :type "submit" :value "Homepage" :class "btn btn-danger"}]]]))
(def result-button (html
                     [:form {:id "resultdatafrm" :action "/results"}
                      [:input {:id "result-page" :type "submit" :value "Back to results" :class "btn btn-info"}] ]))

(defn empty-player-name []
  [:label {:for "name-cong" :style"color:red"} [:b "You have to insert your name!"] ]
  )

(defn congratulate-panel [pts] (html  [:a {:href "/newGameWithoutSaving"} [:div {:id "congratulate-background"}]]
                                      [:div {:id "congratulate"}
                                       [:a {:id "close-btn" :href "/newGameWithoutSaving"} [:span {:id "close-btn"} "x"]]
                                       [:form {:action "/savegame"}
                                        [:div {:id "congratulate-form"}
                                         [:h1 {:id "cong-h1"} [:b (if (= 0 points) (html [:p {:style "float: left; padding-right: 8px; color: #000099;"} "Next time will be better!"]
                                                                                         [:p (str " You won " pts " points.")])
                                                                                   (html [:p {:style "float: left; padding-right: 8px; color: #218838;"} "Congratulations!"]
                                                                                         [:p (str " You won " pts " points.")]))]]
                                         [:div {:id "cong-left" :class "form-group"}
                                          [:label {:for "name-cong"} [:b "Name:"]]
                                          [:input {:type "text" :name "playername" :class "form-control" :id "name-cong" :value playername} ]
                                          (if save (empty-player-name))
                                          ]
                                         [:input {:id "save-game" :type "submit" :value "Save game" :class "btn btn-success"}]]]
                                       ]))

(def choose-table (html [:div {:id "choose-options"}
                         [:div {:id "ch-signs"}
                          [:div {:id "ch-signs-buttons"}
                           [:a {:class "images" :href "/newGame/@"} [:img {:class "imgs" :src "images/smile.png"}]]
                           [:a {:class "images" :href "/newGame/+"} [:img {:class "imgs" :src "images/karo.png"}]]
                           [:a {:class "images" :href "/newGame/-"} [:img {:class "imgs" :src "images/tref.png"}]]
                           [:a {:class "images" :href "/newGame/$"} [:img {:class "imgs" :src "images/srce.png"}]]
                           [:a {:class "images" :href "/newGame/!"} [:img {:class "imgs" :src "images/pik.png"}]]
                           [:a {:class "images" :href "/newGame/*"} [:img {:class "imgs" :src "images/zvezda.png"}]]]
                          ]
                         [:div {:id "delete-sign"}
                          [:div {:id "delete-sign-button"}
                           [:a {:class "images" :href "/newGame/remove"} [:img {:class "imgs" :src "images/remove.png"}]]]]
                         ]


                        ))

(defn get-picture-from-sign [sign]
  (if (= "" sign) (def slika "images/prazna.png"))
  (if (= "+" sign) (def slika "images/karo.png"))
  (if (= "-" sign) (def slika "images/tref.png"))
  (if (= "*" sign) (def slika "images/zvezda.png"))
  (if (= "$" sign) (def slika "images/srce.png"))
  (if (= "@" sign) (def slika "images/smile.png"))
  (if (= "!" sign) (def slika "images/pik.png"))
  slika
  )



(defn create-table [dok] (try
                           (html [:table {:id "tableskocko" :style "border: 1px solid #000000"}
                                  [:tr {:id "tr1"}
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 0 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 1 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 2 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 3 ""))}]]]
                                  [:tr
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 4 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 5 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 6 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 7 ""))}]]]
                                  [:tr
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 8 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 9 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 10 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 11 ""))}]]]
                                  [:tr
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 12 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 13 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 14 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 15 ""))}]]]
                                  [:tr
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 16 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 17 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 18 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 19 ""))}]]]
                                  [:tr
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 20 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 21 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 22 ""))}]]
                                   [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") 23 ""))}]]]
                                  ]
                                 )
                           (catch Exception e
                             (throw (Exception. (str "Dogodila se greska prilikom crtanja tabele! " e))))))

(defn get-circle-from-sign [sign]
  (if (= "" sign) (def slika "images/krug-crni.png"))
  (if (= "z" sign) (def slika "images/krug-zuti.png"))
  (if (= "c" sign) (def slika "images/krug-crveni.png"))
  slika
  )

(defn correct-answer-table [dokument] (try
                                        (html [:table {:id "tableskockocheck" :style "border: 1px solid #000000"}

                                               (if (and (not (nil? (get dokument "signs")))
                                                        (not (empty? (nth (get dokument "signs") 0 "")))
                                                        (not (empty? (nth (get dokument "signs") 1 "")))
                                                        (not (empty? (nth (get dokument "signs") 2 "")))
                                                        (not (empty? (nth (get dokument "signs") 3 ""))))
                                                 (do (def comb (check-combination (nth (get dokument "signs") 0)
                                                                                  (nth (get dokument "signs") 1)
                                                                                  (nth (get dokument "signs") 2)
                                                                                  (nth (get dokument "signs") 3)))
                                                     (if (= comb ["c" "c" "c" "c"]) (do (def finished true)
                                                                                        (def points 35)))
                                                     [:tr
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 0))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 1))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 2))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 3))}]]])
                                                 [:tr
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]])

                                               (if (and (not (nil? (get dokument "signs")))
                                                        (not (empty? (nth (get dokument "signs") 4 "")))
                                                        (not (empty? (nth (get dokument "signs") 5 "")))
                                                        (not (empty? (nth (get dokument "signs") 6 "")))
                                                        (not (empty? (nth (get dokument "signs") 7 ""))))
                                                 (do (def comb (check-combination (nth (get dokument "signs") 4)
                                                                                  (nth (get dokument "signs") 5)
                                                                                  (nth (get dokument "signs") 6)
                                                                                  (nth (get dokument "signs") 7)))
                                                     (if (= comb ["c" "c" "c" "c"]) (do (def finished true)
                                                                                        (def points 30)))
                                                     [:tr
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 0))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 1))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 2))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 3))}]]])
                                                 [:tr
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]])

                                               (if (and (not (nil? (get dokument "signs")))
                                                        (not (empty? (nth (get dokument "signs") 8 "")))
                                                        (not (empty? (nth (get dokument "signs") 9 "")))
                                                        (not (empty? (nth (get dokument "signs") 10 "")))
                                                        (not (empty? (nth (get dokument "signs") 11 ""))))
                                                 (do (def comb (check-combination (nth (get dokument "signs") 8)
                                                                                  (nth (get dokument "signs") 9)
                                                                                  (nth (get dokument "signs") 10)
                                                                                  (nth (get dokument "signs") 11)))
                                                     (if (= comb ["c" "c" "c" "c"]) (do (def finished true)
                                                                                        (def points 25)))
                                                     [:tr
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 0))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 1))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 2))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 3))}]]])
                                                 [:tr
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]])

                                               (if (and (not (nil? (get dokument "signs")))
                                                        (not (empty? (nth (get dokument "signs") 12 "")))
                                                        (not (empty? (nth (get dokument "signs") 13 "")))
                                                        (not (empty? (nth (get dokument "signs") 14 "")))
                                                        (not (empty? (nth (get dokument "signs") 15 ""))))
                                                 (do
                                                   (def comb (check-combination (nth (get dokument "signs") 12)
                                                                                (nth (get dokument "signs") 13)
                                                                                (nth (get dokument "signs") 14)
                                                                                (nth (get dokument "signs") 15)))
                                                   (if (= comb ["c" "c" "c" "c"]) (do (def finished true)
                                                                                      (def points 20)))
                                                   [:tr
                                                    [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 0))}]]
                                                    [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 1))}]]
                                                    [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 2))}]]
                                                    [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 3))}]]])
                                                 [:tr
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]])

                                               (if (and (not (nil? (get dokument "signs")))
                                                        (not (empty? (nth (get dokument "signs") 16 "")))
                                                        (not (empty? (nth (get dokument "signs") 17 "")))
                                                        (not (empty? (nth (get dokument "signs") 18 "")))
                                                        (not (empty? (nth (get dokument "signs") 19 ""))))
                                                 (do
                                                   (def comb (check-combination (nth (get dokument "signs") 16)
                                                                                (nth (get dokument "signs") 17)
                                                                                (nth (get dokument "signs") 18)
                                                                                (nth (get dokument "signs") 19)))
                                                   (if (= comb ["c" "c" "c" "c"]) (do (def finished true)
                                                                                      (def points 15)))
                                                   [:tr
                                                    [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 0))}]]
                                                    [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 1))}]]
                                                    [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 2))}]]
                                                    [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 3))}]]])
                                                 [:tr
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]])

                                               (if (and (not (nil? (get dokument "signs")))
                                                        (not (empty? (nth (get dokument "signs") 20 "")))
                                                        (not (empty? (nth (get dokument "signs") 21 "")))
                                                        (not (empty? (nth (get dokument "signs") 22 "")))
                                                        (not (empty? (nth (get dokument "signs") 23 ""))))
                                                 (do (def comb (check-combination (nth (get dokument "signs") 20)
                                                                                  (nth (get dokument "signs") 21)
                                                                                  (nth (get dokument "signs") 22)
                                                                                  (nth (get dokument "signs") 23)))
                                                     (if (= comb ["c" "c" "c" "c"]) (do (def finished true)
                                                                                        (def points 10))
                                                                                    (do (def finished true)
                                                                                        (def points 0)))
                                                     [:tr
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 0))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 1))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 2))}]]
                                                      [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 3))}]]])
                                                 [:tr
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]
                                                  [:td [:img {:class "imgs" :src (get-circle-from-sign "")}]]])
                                               ]
                                              )
                                        (catch Exception e
                                          (println e))))

(def custom-formatter (formatter "dd.MM.yyyy HH:mm"))

(defn show-games [documents]
  (html
    [:div {:id "table-show-games"}
     [:h1 {:style "color: #000099; margin-left: 37%"} [:b "Game results"] ]
     [:table {:id "result-table" :class "table table-bordered table-hover table-sm"}
      [:thead {:id "table-head" :class "thead-dark"}
       [:tr
        [:th {:scope "col"} "Name"  ]
        [:th {:scope "col"} "Points"  ]
        [:th {:scope "col"} "Date" ]
        [:th {:scope "col"} "Delete game"  ]
        [:th {:scope "col"} "Show game"]
        ]]

      (into [:tbody]
            (try
              (for [play documents]
                [:tr {:scope "row"}
                 [:td (:name play)]
                 [:td (:points play)]
                 [:td (if (not (nil? (:date play))) (unparse custom-formatter (:date play)))]
                 [:td [:a {:href (str "/deleteGame/" (h (:_id play))) :style "color:purple;"}
                       [:input {:type "submit" :class "btn btn-danger" :value "Delete" :style "width: 100px;"}]]]
                 [:td [:a {:href (str "/viewGame/" (h (:_id play))) :style "color:purple;"}
                       [:input {:type "submit" :class "btn btn-info" :value "Show" :style "width: 100px;"}]]]])
              (catch Exception e
                [[:b "Dogodila se greska prilikom ucitavanja igara!"]]
                ;(throw (Exception. e))
                ))
            )]
     [:br]
     ]))

(defn show-combination [comb]
  (try
    (html [:table {:id "tableskockocomb" :style "border: 1px solid #000000; margin-top: 5px;"}
           [:tr
            [:td [:img {:class "imgs" :src (get-picture-from-sign (nth comb 0))}]]
            [:td [:img {:class "imgs" :src (get-picture-from-sign (nth comb 1))}]]
            [:td [:img {:class "imgs" :src (get-picture-from-sign (nth comb 2))}]]
            [:td [:img {:class "imgs" :src (get-picture-from-sign (nth comb 3))}]]]]
          )
    (catch Exception e
      (throw (Exception. (str "Dogodila se greska prilikom crtanja tabele! " e)))))
  )

(defn show-game-info [dokum]
  (try
    (html [:div {:id "game-info"}
           [:div {:id "game-info-data"}
            [:p [:b {:style "color: #000099;"} "Name: "] (get dokum "name")]
            [:p [:b {:style "color: #000099;"} "Points: "] (get dokum "points")]
            [:p [:b {:style "color: #000099;"} "Date: "] (unparse custom-formatter (:date dokum))]]
           ]
          )
    (catch Exception e
      (throw (Exception. (str "Dogodila se greska prilikom crtanja tabele! " e))))))

(defn welcome
  "A ring handler to process all requests sent to the webapp"
  [request]
  (try
    (if (and (not (nil? dok)) (not preview)) (db/delete-game-full-id dok))
    (def dok nil)
    (page/pageWelcome "Skočko"
                      (html [:div {:id "welcome-title"}
                             [:h1 {:style "color: #000099; margin-left: 13%;"} "Welcome to Skočko"]]
                            [:div {:id "hp-buttons"}
                             [:form {:id "newGameForm" :action "/newGame"}
                              [:input {:type "submit" :value "New game" :class "btn btn-success" :style "width: 150px;"}] ]
                             [:br]
                             [:form {:id "reultdataForm" :action "/results"}
                              [:input {:type "submit" :value "Show results" :class "btn btn-info" :style "width: 150px;"}] ]]
                            ))
    (catch Exception e
      (throw (Exception. e))))
  )



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
                            (do (def doc (db/find-doc-by-id dok))
                                (db/remove-sign dok (last (get doc "signs")))))
                          (db/insert-sign dok sign))
    (def doc (db/find-doc-by-id dok))
    (if (check-if-fourth doc) (do (def answer-table (correct-answer-table doc))
                                  answer-table))
    (redirect "/newGame")
    (catch Exception e
      (throw (Exception. e))))
  )

(defn save-game [request]
  (try
    (def playername (clojure.string/replace (subs (get-in request [:query-string]) 11) "+" " "))

    (if (or (nil? playername) (empty? (clojure.string/trim playername))) (def save true)
                                                                         (do (db/insert-game-data dok playername points combination)
                                                                             (def dok nil)
                                                                             ))
    (redirect "/newGame")
    (catch Exception e
      (throw (Exception. e))))
  )

(defn get-all-docs []
  (try
    (def documents (db/get-all-documents))
    (page/pageShowResults "Skočko - results"
                          (str (html [:div {:id "hp-btn-result"} home-page-button] ) (show-games documents)))
    (catch Exception e
      (throw (Exception. e))))
  )

(defn delete-game [id]
  (try
    (db/delete-game id)
    (redirect "/results")
    (catch Exception e
      (throw (Exception. e))))
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
      (throw (Exception. e))))
  )

(defroutes route-defs
           (GET "/" [] welcome)
           (GET "/newGame" [] (try
                                (if (nil? dok) (do (def dok (db/create-new-doc))
                                                   (def combination (random-table sign-list))
                                                   (def answer-table (correct-answer-table dok))
                                                   (def finished false)
                                                   (def points 0)
                                                   (def preview false)
                                                   (def save false)
                                                   (def playername "")))
                                (def docc (db/find-doc-by-id dok))
                                (page/pageNewGame "Skočko - New game"
                                                  (str home-page-button (if preview result-button)
                                                       (html [:div {:id "main-table"}
                                                              (create-table docc)
                                                              (if finished (show-combination combination))
                                                              ;(show-combination combination)
                                                              ]) answer-table
                                                       (if (not finished) choose-table)
                                                       (if (and finished (not preview)) (congratulate-panel points))
                                                       (if preview (show-game-info docc))))
                                (catch Exception e)))

           (GET "/newGame/:sign" [sign] (try
                                          (update-sign dok sign)
                                          (catch Exception e)))
           (GET "/savegame" [] (try
                                 save-game
                                 (catch Exception e)))
           (GET "/results" [] (try
                                (get-all-docs)
                                (catch Exception e)))

           (GET "/deleteGame/:id" [id] (try
                                         (delete-game id)
                                         (catch Exception e)))
           (GET "/viewGame/:id" [id] (try
                                       (view-game id)
                                       (catch Exception e)))
           (GET "/newGameWithoutSaving" [] (try
                                             (db/delete-game-full-id dok)
                                         (def dok nil)
                                         (redirect "/newGame")
                                       (catch Exception e)))

           (GET "/hello/:name&:surname" [] hello)
           (not-found "<h1>This is not the page you are looking for</h1>
              <p>Sorry, the page you requested was not found!</p>")
           )

