(ns magewars-deckbuilder.server
  (:require
    [ring.adapter.jetty               :refer [run-jetty]]
    [ring.middleware.resource         :refer [wrap-resource]]
    [ring.middleware.session          :refer [wrap-session]]
    [ring.middleware.session.cookie   :refer [cookie-store]]
    [ring.middleware.file             :refer [wrap-file]]
    [ring.middleware.file-info        :refer [wrap-file-info]]
    [tailrecursion.castra.handler     :refer [castra]]))

(def server (atom nil))

(defn app [port public-path]
  (->
    (castra 'magewars-deckbuilder.api.deck)
    (wrap-session {:store (cookie-store {:key "a 16-byte secret"})})
    (wrap-file public-path)
    (wrap-file-info)
    (run-jetty {:join? false :port port})))

(defn start-server
  "Start castra demo server (port 5050)."
  [port public-path]
  (swap! server #(or % (app port public-path))))

(defn start-task [{:keys [port public]
                 :or {port 5050
                      public "resources/public"}
                 :as boot}]
  (.mkdirs (java.io.File. public))
  (start-server port public)
  (fn [continue]
    (fn [event]
      (continue event))))
