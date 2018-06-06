(ns skocko.pages.forms
  (:use
    [hiccup.core :refer :all]
    [hiccup.page :refer :all]
    [hiccup.bootstrap.page :refer :all]
    [hiccup.bootstrap.element :refer :all]
    [clj-time.format :refer :all]))

(defn home-page-button [] (html  [:div {:id "homep"}
                              [:form {:id "home-page-form" :action "/"}
                               [:input {:id "home-page" :type "submit" :value "Homepage" :class "btn btn-danger"}]]]))

(defn result-button [] (html
                     [:form {:id "resultdatafrm" :action "/results"}
                      [:input {:id "result-page" :type "submit" :value "Back to results" :class "btn btn-info"}] ]))

(defn empty-player-name []
  [:label {:for "name-cong" :style"color:red"} [:b "You have to insert your name!"] ]
  )

(defn choose-table [] (html [:div {:id "choose-options"}
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
                           [:a {:class "images" :href "/newGame/remove"} [:img {:class "imgs" :src "images/remove.png"}]]]]]))

(defn get-picture-from-sign [sign]
  (case sign
    "" "images/prazna.png"
    "+" "images/karo.png"
    "-" "images/tref.png"
    "*" "images/zvezda.png"
    "$" "images/srce.png"
    "@" "images/smile.png"
    "!" "images/pik.png"))

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

(defn create-row [dok rb]
  (let [first-element (* 4 rb) last-element (+ first-element 3) range (range first-element (+ last-element 1))]
    [:tr
     (for [j range]
       [:td [:img {:class "imgs" :src (get-picture-from-sign (nth (get dok "signs") j ""))}]])
     ]
    ))

(defn create-table [dok] (try
                           (html [:table {:id "tableskocko" :style "border: 1px solid #000000"}
                                  (for [i (range 0 6)]
                                    (create-row dok i))

                                  ]
                                 )
                           (catch Exception e
                             (str "Dogodila se greska prilikom crtanja tabele! " e))))

(defn welcome-form []
  (html [:div {:id "welcome-title"}
         [:h1 {:style "color: #000099; margin-left: 13%;"} "Welcome to Skočko"]]
        [:div {:id "hp-buttons"}
         [:form {:id "newGameForm" :action "/newGame"}
          [:input {:type "submit" :value "New game" :class "btn btn-success" :style "width: 150px;"}] ]
         [:br]
         [:form {:id "reultdataForm" :action "/results"}
          [:input {:type "submit" :value "Show results" :class "btn btn-info" :style "width: 150px;"}] ]]
        ))

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
                (throw (Exception. (str "Dogodila se greska prilikom ucitavanja igara! " e)))
                ))
            )]
     [:br]
     ]))

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

(defn congratulate-panel [playername points save] (html  [:a {:href "/newGameWithoutSaving"} [:div {:id "congratulate-background"}]]
                                      [:div {:id "congratulate"}
                                       [:a {:id "close-btn" :href "/newGameWithoutSaving"} [:span {:id "close-btn"} "x"]]
                                       [:form {:action "/savegame"}
                                        [:div {:id "congratulate-form"}
                                         [:h1 {:id "cong-h1"} [:b (if (= 0 points) (html [:p {:style "float: left; padding-right: 8px; color: #000099;"} "Next time will be better!"]
                                                                                         [:p (str " You won " points " points.")])
                                                                                   (html [:p {:style "float: left; padding-right: 8px; color: #218838;"} "Congratulations!"]
                                                                                         [:p (str " You won " points " points.")]))]]
                                         [:div {:id "cong-left" :class "form-group"}
                                          [:label {:for "name-cong"} [:b "Name:"]]
                                          [:input {:type "text" :name "playername" :class "form-control" :id "name-cong" :value playername} ]
                                          (if save (empty-player-name))
                                          ]
                                         [:input {:id "save-game" :type "submit" :value "Save game" :class "btn btn-success"}]]]
                                       ]))

(defn get-circle-from-sign [sign]
  (case sign
    "" "images/krug-crni.png"
    "z" "images/krug-zuti.png"
    "c" "images/krug-crveni.png"))

(defn correct-table-row [comb]
  [:tr
   [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 0 ""))}]]
   [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 1 ""))}]]
   [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 2 ""))}]]
   [:td [:img {:class "imgs" :src (get-circle-from-sign (nth comb 3 ""))}]]])