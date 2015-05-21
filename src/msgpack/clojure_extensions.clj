(ns msgpack.clojure-extensions
  "MessagePack extensions for Clojure-specific types"
  (:require [msgpack.core :as msg]
            [msgpack.macros :refer [extend-msgpack]]))

(extend-msgpack
 clojure.lang.Keyword
 3
 [bytes] (keyword (msg/unpack bytes))
 [kw] (msg/pack (name kw)))

(extend-msgpack
 clojure.lang.Symbol
 4
 [bytes] (symbol (msg/unpack bytes))
 [sym] (msg/pack (name sym)))
