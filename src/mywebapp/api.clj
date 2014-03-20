(ns mywebapp.api
  (:require [clj-http.client :as client]))
;;Needs an API key string to be added
;;Get a key from https://www.comicvine.com/api/
(def api-key "YOUR KEY HERE")

(defn api-request 
  [url]
  (get-in
    (client/request
      {:url url
       :method :get
       :content-type :json
       :as :json}) [:body :results]))

(defn filter-results
  "Returns the first result that meets the criterion."
  [results criterion]
  (take 1 
        (filter 
          #(and (not-any? nil? (vals %))
                (= (get-in % [:publisher :name]) criterion))
          results)))

(defn less-filter-results
  "No criterion; probably begging to be multi-aritized."
  [results]
  (take 1
        (filter
          #(and (not-any? nil? (vals %))
                (> (:count_of_issue_appearances %) 20))
          results)))

(defn build-url
  "Returns a url with randomized query string for the api request."
  []
  (str "http://www.comicvine.com/api/characters/"
      "?api_key="
      api-key
      "&field_list=deck,site_detail_url,name,image,first_appeared_in_issue,count_of_issue_appearances,publisher"
      "&format=json&limit=20&offset=" (rand-int 70802)))

(defn build-char-url
  [id]
  (str "http://www.comicvine.com/api/issue/4000-"
       id
       "/?api_key="
       api-key
       "&format=json&field_list=volume,issue_number,cover_date"))

(defn build-various-url
  "Similar to the build-url function but can return more than one type of resource. Possible multiple arity merge later."
  [resource total-results]
  (str "http://www.comicvine.com/api/"
       resource
       "/?api_key="
       api-key
       "&field_list=deck,site_detail_url,name,image,count_of_issue_appearances&format=json&limit=10&offset="
       (rand-int total-results)))
  
;;&field_list=name,count_of_issue_appearances,deck,gender,image,site_detail_url,publisher

(defn return-result
  [criterion count]
  (->> (repeatedly count #(filter-results (api-request (build-url)) criterion))
       (filter (complement empty?))
       (first)
       (into {})))

(defn less-return-result
  [count resource total-results]
  (->> (repeatedly count #(less-filter-results (api-request (build-various-url resource total-results))))
       (filter (complement empty?))
       (first)
       (into {})))
       

(defn character-hash
  [{:keys [deck name image first_appeared_in_issue count_of_issue_appearances site_detail_url]}]
  (let [x (api-request (build-char-url (:id first_appeared_in_issue)))]
  (assoc {} :desc deck :name name :pic (get-in image [:super_url]) :first-issue first_appeared_in_issue 
         :appearances count_of_issue_appearances :link site_detail_url :date (subs (:cover_date x) 0 4) 
         :comic (get-in x [:volume :name]) :number (:issue_number x))))

(defn murder-hash
  []
  (let [x (future (less-return-result 10 "characters" 70802))
        y (future (less-return-result 10 "objects" 2350))
        z (future (less-return-result 10 "locations" 3065))]
    (assoc {} :character @x :object @y :location @z)))

(defn murder-hash-nf
  "Just to try it without futures."
  []
  (let [x (less-return-result 10 "characters" 70802)
        y (less-return-result 10 "objects" 2350)
        z (less-return-result 10 "locations" 3065)]
    (assoc {} :character x :object y :location z)))
