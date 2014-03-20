(defproject mywebapp "0.1.0-SNAPSHOT"
  :description "The hero machine"
  :url "http://heromachine.stuwest.cloudbees.net/"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.1"]
                 [hiccup "1.0.1"]
                 [clj-http "0.7.6"]]
  :plugins [[lein-ring "0.7.3"]]
  :ring {:handler mywebapp.routes/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})
