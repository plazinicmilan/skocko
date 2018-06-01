(ns skocko.core
  (:use ring.adapter.jetty)
  (:require
            [compojure.core :refer [routes]]
            [skocko.router.route :refer [route-defs]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]

           ))

(def app
  (routes #'route-defs))

(defn -main
  "A very simple web server using Ring & Jetty"
  [port]
  (jetty/run-jetty app
                   {:port (Integer. port)})
  )

(defn -dev-main
  "A very simple web server using Ring & Jetty that reloads code changes via the development profile of Leiningen"
  [port]
  (jetty/run-jetty (wrap-reload #'app)
                   {:port (Integer. port)}))

