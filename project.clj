(defproject skocko "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]  [ring "1.4.0"]
                 [compojure "1.6.1"] [hiccup "1.0.5"] [org.clojars.yaska80/monger "3.1.0-SNAPSHOT"]]

  :main skocko.core
  :profiles {:dev
             {:main skocko.core/-dev-main}})

