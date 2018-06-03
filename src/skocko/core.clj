(ns skocko.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [skocko.router.route :refer [route-defs]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))


(def app
  (wrap-defaults route-defs site-defaults))
