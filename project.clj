(defproject skocko "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"] [ring "1.4.0"]
                 [compojure "1.6.1"] [hiccup "1.0.5"] [org.clojars.yaska80/monger "3.1.0-SNAPSHOT"]
                 [clj-time "0.14.4"] [cheshire "5.8.0"] [net.mikera/imagez "0.12.0"]
                 [hiccup-table "0.2.0"]
                 [hiccup-bootstrap "0.1.2"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler skocko.core/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
