(ns mywebapp.routes
  (:use compojure.core
        mywebapp.views)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))


(defroutes app-routes
  (GET "/random" [] random-page)
  (POST "/random" [publisher] (result-page publisher))
  (GET "/about" [] about-page)
  (GET "/contact" [] contact-page)
  (GET "/murder" [] murder-page)
  (POST "/murder" [] murder-results)
  (GET "/" [] index-page)
  (route/resources "/")
  (route/not-found "No page"))


(def app
  (handler/site app-routes))
