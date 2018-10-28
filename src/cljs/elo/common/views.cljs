(ns elo.common.views
  (:require [elo.utils :as utils]))

(defn drop-down
  [opts dispatch-key value & {:keys [value-fn display-fn caption]
                              :or {value-fn identity
                                   caption ""
                                   display-fn identity}}]

  (into [:select
         {:on-change (utils/set-val dispatch-key) :value (or value "")}]

        (cons [:option {:disabled true
                        :selected false
                        :value caption}
               caption]
              (for [o opts]
                [:option {:value (value-fn o)} (display-fn o)]))))
