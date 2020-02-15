(ns generative-art-live.prod
  (:require [generative-art-live.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
