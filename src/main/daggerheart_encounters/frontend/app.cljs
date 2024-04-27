(ns daggerheart-encounters.frontend.app
  (:require [clojure.edn :as edn]
            [reagent.core :as r]
            [reagent.dom :as rdom]))

;; State
(defonce app-state (r/atom
                    (or (let [params (.get (new js/URLSearchParams js/window.location.search) "state")]
                          (edn/read-string params))
                        {})))

;; Views
(defn <main>
  []
  (let [counter (r/cursor app-state [:counter])]
    [:<>
     [:h1 "Hi"]
     [:button {:on-click #(swap! counter  inc)} "Click me"]
     [:pre {} @app-state]]))

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
