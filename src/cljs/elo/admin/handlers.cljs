(ns elo.admin.handlers
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [elo.common.handlers :as common]))

(def page ::page-id)

(def getter (partial common/getter* page))

(def setter (partial common/setter* page))

(def default-player
  {:name ""
   :email ""
   :league_id nil})

(def default-db
  {:player default-player
   :leagues []})

(rf/reg-event-db ::name (setter [:player :name]))
(rf/reg-event-db ::email (setter [:player :email]))
(rf/reg-event-db ::league (setter [:player :league_id]))

(rf/reg-sub ::league (getter [:league]))
(rf/reg-sub ::leagues (getter [:leagues]))
(rf/reg-sub ::player (getter [:player]))

(rf/reg-sub ::valid-player?
            (fn [db _]
              (not-any? #(= % "")
                        (vals (common/get-in* db page [:player])))))

(rf/reg-event-fx ::add-player-success
                 (fn [{:keys [db]} _]
                   (js/alert "Thanks")))

(rf/reg-event-db ::reset-player
                 (fn [db _]
                   (common/assoc-in* db page [:player] default-player)))

(defn writer
  [page uri on-success]
  (fn [{:keys [db]} _]
    {:db db
     :http-xhrio {:method :post
                  :uri uri
                  :params (common/get-in* db page [:player])
                  :format (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success [on-success]
                  :on-failure [:failed]}}))

(rf/reg-event-fx ::add-player (writer page
                                      "/api/add-player"
                                      ::add-player-success))

(rf/reg-event-db ::load-leagues-success
                 (setter [:leagues]))

(rf/reg-event-fx ::load-leagues
                 (common/loader-no-league-id
                  page
                  "/api/leagues"
                  ::load-leagues-success))

(rf/reg-event-db ::initialize-db
                 (fn [db _]
                   (assoc db page default-db)))
