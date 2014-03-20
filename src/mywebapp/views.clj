(ns mywebapp.views
  (:use [hiccup core page form]
        [mywebapp.api :only [return-result character-hash murder-hash]]))

(defn header
  []
  [:div {:class "navbar navbar-default"}
         [:div {:class "navbar-header"}
          [:button {:type "button" :class "navbar-toggle" :data-toggle "collapse" :data-target ".navbar-collapse"}
           [:span {:class "icon-bar"}]
           [:span {:class "icon-bar"}]
           [:span {:class "icon-bar"}]]
          [:a {:class "navbar-brand" :href "/"} "Hero Machine"]]
         [:div {:class "navbar-collapse collapse"}
          [:ul {:class "nav navbar-nav"}
           [:li [:a {:href "/"} "Home"]]
           [:li [:a {:href "/random"} "The Randomizer"]]
           [:li [:a {:href "/murder"} "Comic Book Murder Mystery"]]
           [:li [:a {:href "/about"} "About"]]
           [:li [:a {:href "/contact"} "Contact"]]]]])

(defn template
  [& body]
  (html5
      [:head
       [:meta {:charset "utf-8"}]
       [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
       [:meta {:name "description" :content ""}]
       [:meta {:name "author" :content ""}]
       [:title "The random hero machine"]
       (include-css "css/bootstrap.min.css" "css/navbar.css")
       (include-js "js/jquery.js" "js/bootstrap.min.js")]
      [:body
       [:div {:class "container"}
        (header)
        body]
       [:script {:src "js/button.js" :type "text/javascript"}]]))

(defn random-page
  [request]
  (template
        [:div {:class "jumbotron"}
         [:h1 "The Randomizer."]
         [:p (str "A random comic book character from the thousands of heroes, villains, obscure supporting cast members, celebrity cameos "
                  "and sundry players from licensed tie-in comics published by the big two superhero companies over the years. Just select "
                  "your preferred publisher below.")]
         [:form {:action "/random" :method "post"}
       ;   [:div {:class "form-inline"}
       ;    [:div {:class "radio"}
      ;      [:label [:input {:checked "true" :id "1-1" :name "1" :type "radio" :value "1"}] " Marvel "]
       ;     [:label [:input {:id "1-2" :name "1" :type "radio" :value "2"}] " DC Comics "]]]
          [:button {:class "btn btn-lg btn-primary" :name "publisher" :type "submit" :value "DC Comics"} "DC Comics"]
       [:span "&nbsp;"]
          [:button {:class "btn btn-lg btn-primary" :name "publisher" :type "submit" :value "Marvel"} "Marvel"]]]))

(defn pre-result-page
  [publisher]
  (template
    [:div {:class "jumbotron"}
     [:pre (with-out-str (clojure.pprint/pprint (character-hash (return-result publisher 10))))]]))

(defn about-page
  [request]
  (template
    [:div {:class "jumbotron"}
     [:h2 "Site credits"]
     [:ul
      [:li "Created by " [:a {:href "/contact"} "Stu West"] "."]
      [:li "Coded in " [:a {:href "http://clojure.org/"} "Clojure"] " &mdash; Lisp for the Java Virtual Machine."]
      [:li "Comics data via the " [:a {:href "http://www.comicvine.com/api/"} "Comic Vine API"] "."]
      [:li "Site template using " [:a {:href "http://getbootstrap.com"} "Bootstrap"] "."]
      [:li "Hosting by " [:a {:href "http://www.cloudbees.com"} "Cloudbees"] "."]]]))

(defn murder-page
  [request]
  (template
    [:div {:class "jumbotron"}
     [:div {:class "row"}
      [:div {:class "col-md-6"}
       [:h1 "Who killed Dr Longbox?"]
       [:h3 (str "Mild-mannered Dr Longbox locked himself into his study to spend some quality time with "
                 "his comic book collection. No one entered or left the room, but when his relatives "
                 "broke down the door hours later they found he had been the victim of a brutal murder. Luckily, plucky "
                 "Constable Nearmint thinks he can program his Crime Computer to calculate whodunnit.")]
       [:form {:action "/murder" :method "post"}
        [:button {:class "btn btn-lg btn-primary" :data-loading-text "Calculating..." :name "murderer" :type "submit" :value "whodunnit"} "Identify the culprit."]]]
      [:div {:class "col-md-6"}
       [:img {:src "img/dr_longbox.jpg" :class "thumbnail responsive"}]]]]))

(defn murder-results
  [request]
  (template
    (let [results (murder-hash)]
    [:div {:class "jumbotron"}
     [:h1 "Who killed Dr Longbox?"]
     [:div {:class "row"}
      [:div {:class "col-lg-4"}
       [:h2 {:class "text-center"} "Murderer"]
       [:div {:class "panel-group" :id "murderer"}
        [:div {:class "panel panel-default"}
         [:div {:class "panel-heading"}
          [:h4 {:class "panel-title"}
           [:a {:class "accordion-toggle" :data-toggle "collapse" :data-parent "#murderer" :href "#murderOne"}
            (get-in results [:character :name])]]]
         [:div {:id "murderOne" :class "panel-collapse collapse"}
          [:div {:class "panel-body"}
           (get-in results [:character :deck])
           [:a {:href (get-in results [:character :site_detail_url])} "+"]]]]]
       [:img {:src (get-in results [:character :image :medium_url]) :class "thumbnail responsive"}]]
      [:div {:class "col-lg-4"}
       [:h2 {:class "text-center"} "Location"]
       [:div {:class "panel-group" :id "location"}
        [:div {:class "panel panel-default"}
         [:div {:class "panel-heading"}
          [:h4 {:class "panel-title"}
           [:a {:class "accordion-toggle" :data-toggle "collapse" :data-parent "#location" :href "#locationOne"}
            (get-in results [:location :name])]]]
         [:div {:id "locationOne" :class "panel-collapse collapse"}
          [:div {:class "panel-body"}
           (get-in results [:location :deck])
           [:a {:href (get-in results [:location :site_detail_url])} "+"]]]]]
       [:img {:src (get-in results [:location :image :medium_url]) :class "thumbnail responsive"}]]
      [:div {:class "col-lg-4"}
       [:h2 {:class "text-center"} "Weapon"]
       [:div {:class "panel-group" :id "weapon"}
        [:div {:class "panel panel-default"}
         [:div {:class "panel-heading"}
          [:h4 {:class "panel-title"}
           [:a {:class "accordion-toggle" :data-toggle "collapse" :data-parent "#weapon" :href "#weaponOne"}
            (get-in results [:object :name])]]]
         [:div {:id "weaponOne" :class "panel-collapse collapse"}
          [:div {:class "panel-body"}
           (get-in results [:object :deck])
           [:a {:href (get-in results [:object :site_detail_url])} "+"]]]]]
       [:img {:src (get-in results [:object :image :medium_url]) :class "thumbnail responsive"}]]]
     [:br "&nbsp;"]
      [:form {:action "/murder" :method "post"}
          [:button {:class "btn btn-lg btn-primary" :data-loading-text "Sleuthing..." :name "murderer" :type "submit" :value "whodunnit"} "I demand another solution"]]])))
    
(defn contact-page
  [request]
  (template
    [:div {:class "jumbotron"}
     [:h1 "Contact Stu West"]
     [:div {:class "row"}
      [:div {:class "col-md-3"}
       [:img {:src "img/portrait.jpg" :class "responsive thumbnail"}]]
      [:div {:class "col-md-9"}
       [:ul
        [:li [:a {:href "https://twitter.com/stuwest"} "Tweet"] " at me."]
        [:li "Ask me a question " [:a {:href "http://reviewallthethings.tumblr.com/ask"} "via Tumblr"] "."]]]]]))

(defn index-page
  [request]
  (template
    [:div {:class "jumbotron"}
     [:h1 "Hero Machine frivolities:"]
     [:h2 [:a {:href "/random"} "The Randomizer"]]
     [:p "For you, a random Marvel or DC character from a cast of over 70,000."]
     [:h2 [:a {:href "/murder"} "Comic Book Murder Mystery"]]
     [:p "Who killed Dr Longbox? (Also where and what.)"]]))

(defn result-page
  [publisher]
  (let [y (character-hash (return-result publisher 10))]
    (template
      [:div {:class "jumbotron"}
       [:div {:class "row"}
        [:div {:class "col-md-3"}
         [:img {:src (:pic y) :class "img-thumbnail img-responsive"}]]
        [:div {:class "col-md-9"}
         [:h1 (:name y)]
         [:h2 (:desc y)]
         [:h3 (str "Debuted in " (:comic y) " #" (:number y) " (" (:date y) ") and has appeared a total of " (:appearances y) " time" (if (> (:appearances y) 1) "s." "."))]
         [:h3 [:a {:href (:link y)} "[Permalink]"]]]]
       [:div {:class "row"}
        [:div {:class "col-md-12"}
         [:h2 {:class "text-right"} "Randomize again?"]
         [:form {:action "/random" :method "post" :class "pull-right"}
          [:button {:class "btn btn-lg btn-primary" :name "publisher" :type "submit" :value "DC Comics"} "DC Comics"]
       [:span "&nbsp;"]
          [:button {:class "btn btn-lg btn-primary" :name "publisher" :type "submit" :value "Marvel"} "Marvel"]]]]])))