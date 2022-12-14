(ns supervisor.main
  (:require
    [reagent.dom]
    [re-frame.core :as reframe]
    [secretary.core :as secretary]
    [supervisor.util :as util]
    [supervisor.location :as location]
    [supervisor.unit.page-router :as page-router]
    [supervisor.unit.header :as header]
    [supervisor.data.theme :as d-theme]
    [supervisor.data.route :as d-route]
    [supervisor.side.qdb :as qdb]
    [supervisor.side.ldb :as ldb]
    [supervisor.side.debug]
    [supervisor.side.api]
    [supervisor.environ]
    ))
; (reframe/reg-sub :all (fn [db] db))
; (println  (reframe/subscribe[:all]))

; TODO add date format utils to supervisor.util

;TODO create a way to detect if an element is in the viewport or not
; and create hooks didEnterViewport didLeaveViewport
; and maby allow for triggers above and below (500px up and down)
; and create hook couldEnterViewport (near viewport up or down)
(defn app []
  ; do setup?
  (fn []
    (let [qdb @(qdb/fetch)
          ldb @(ldb/fetch)]
      (util/xxp "ldb" ldb)
      (util/xxp "qdb" qdb)
      [:div.supervisor-container
       [header/unit]
       [page-router/unit]])))

(defn render []
  (println "main/render")
  (js/console.log "%c the follwing render error is not a bug 😘 -dunk ",
                  "font-size: 15px;
                  background: rgb(255,255,255);
                  background: linear-gradient(90deg, rgba(255,255,255,1) 1%, rgba(77,234,242,1) 22%, rgba(255,183,229,1) 53%, rgba(232,235,225,1) 69%, rgba(198,232,229,1) 86%, rgba(0,212,255,1) 100%);
                  color: black;
                  text-align: center;
                  padding: 2px;
                  margin: 10px;
                  border-radius: 13px;"
                  )
  (let [container (js/document.getElementById "supervisor-container")]
    (reagent.dom/render [app] container)))

(defn ^:export ^:dev/before-load before-load
 "hook that runs when before shadow watch reloads"
  []
  (util/xxl "main/before-load"))

(defn ^:export ^:dev/after-load after-load
  "hook that runs after shadow watch reloads"
  []
  (util/xxl "main/after-load")
  (reframe/clear-subscription-cache!)
  (render))

(defn ^:export init
  "init will trigger once on page refresh"
  []
  (util/xxl "main/init ")
  ; setup custom side dbs
  (qdb/hydrate!)
  (ldb/hydrate!)
  ; populate default data
  (d-theme/init)
  (d-route/init)
  ; dispatch window location to router
  (secretary/dispatch! (location/fetch-route))
  (.addEventListener js/window "popstate"
    (fn [] (secretary/dispatch! (location/fetch-route))))
  ;load the app
  (after-load))
