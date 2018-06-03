(ns skocko.pages.preview
  (:require
    [hiccup.core :refer :all]
    [hiccup.page :refer [doctype include-css]]
    [hiccup.page :refer :all]
    [hiccup.form :as f]
    [hiccup.bootstrap.page :refer :all]
    [hiccup.bootstrap.element :as hbe]
    [hiccup.element :refer [link-to]]))


(defn pageNewGame [title & content]
  (str
    (html
      (doctype :html5)
      [:html
       [:head
        ;[:link {:rel "icon" :href "images/skocko.png"}]
        [:title title]
        (include-css "css/skocko.css")
        (include-css "css/background-game.css")
        (include-css "css/bootstrap.min.css")
        [:body content]]])))

(defn pageWelcome [title & content]
  (str
    (html
      (doctype :html5)
      [:html
       [:head
        ; [:link {:rel "icon" :href "images/skocko.png"}]
        [:title title]
        (include-css "css/welcomepage.css")
        (include-css "css/background.css")
        (include-css "css/bootstrap.min.css")
        [:body content]]])))

(defn pageShowResults [title & content]
  (str
    (html
      (doctype :html5)
      [:html
       [:head
        ;  [:link {:rel "icon" :href "images/skocko.png"}]
        [:title title]
        (include-css "css/skocko.css")
        (include-css "css/background.css")
        (include-css "css/bootstrap.min.css")
        [:body content]]])))