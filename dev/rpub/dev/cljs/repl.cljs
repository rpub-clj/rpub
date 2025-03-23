(ns rpub.dev.cljs.repl
  (:require [rpub.admin.dag :as admin-dag]))

(comment
  (:model/content-types-index (:rpub.lib.dag/values @admin-dag/dag-atom))
  (keys *1))
