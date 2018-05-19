(ns skocko.core
  (:use ring.adapter.jetty)
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn welcome
  "A ring handler to process all requests sent to the webapp"
  [request]
  (if (= "/" (:uri request))
  {:status 200
   :body "<h1>Hello, Clojure Worlde</h1>  <p>Welcome to your first Clojure app.  This message is returned regardless of the request, sorry<p>"
   :headers {}}
  {:status 404
   :body "<h1>Thus is not the page you are looking for</h1>
            <p>Sorry, the page you requested was not found!></p>"
   :headers {}}))

(defn -main
  "A very simple web server using Ring & Jetty"
  [port]
  (jetty/run-jetty welcome
    {:port (Integer.  port)})
  )

(defn -dev-main
  "A very simple web server using Ring & Jetty that reloads code changes via the development profile of Leiningen"
  [port]
  (jetty/run-jetty (wrap-reload #'welcome)
                   {:port (Integer. port)}))

