(ns daggerheart-encounters.frontend.app
  (:require [clojure.edn :as edn]
            [reagent.core :as r]
            [reagent.dom :as rdom]))

;; State
(defonce app-state (r/atom
                    (or (let [params (.get (new js/URLSearchParams js/window.location.search) "state")]
                          (edn/read-string params))
                        {:adversaries []})))

;; Views
(defn <adversaries>
  [{:keys [adversaries]}]
  (let [headers ["Name" "Stress" "HP" "Minor" "Major" "Severe"]]
    [:table
     [:thead
      [:tr (for [header headers]
             [:th {:key header} header])]]
     [:tbody
      (doall
       (map-indexed
        (fn [idx adversary]
          [:tr {:key (:name adversary)}
           [:td {:on-click #(let [response (js/prompt "new name:"
                                                      (:name adversary))]
                              (when response
                                (swap! adversaries assoc-in [idx :name]
                                       response)))}
            (:name adversary)]
           [:td (:stress adversary)
            [:button {:on-click #(swap! adversaries update-in [idx :stress] dec)} "-"]
            [:button {:on-click #(swap! adversaries update-in [idx :stress] inc)} "+"]]
           [:td (:hp adversary)
            [:button {:on-click #(swap! adversaries update-in [idx :hp] dec)} "-"]
            [:button {:on-click #(swap! adversaries update-in [idx :hp] inc)} "+"]]])
        @adversaries))]]))

(defn <main>
  []
  (let [adversaries (r/cursor app-state [:adversaries])]
    [:<>
     [:h1 "Adversaries"]
     [<adversaries> {:adversaries adversaries}]
     [:button {:on-click #(swap! adversaries conj {:name "" :hp 0 :stress 0})}
      "add row"]]))

;; Setup

(def ^:dynamic *state-url-sync-tracker* nil)

(defn sync-state-to-url
  []
  (let [state {:state (pr-str @app-state)}
        encoded (.toString (new js/URLSearchParams (clj->js state)))]
    (.pushState js/history (clj->js state) "" (str "?" encoded))))

(defn start []
  (println "Start")
  (let [params (.get (new js/URLSearchParams js/window.location.search) "state")]
    (println (edn/read-string params)))

  (rdom/render [<main>]
               (. js/document (getElementById "root"))))

(defn stop []
  (println "Stop"))

(defn ^:export init []
  (println "Init")
  (r/track! sync-state-to-url)
  (start))
