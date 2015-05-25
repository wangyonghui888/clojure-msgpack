# clojure-msgpack

clojure-msgpack is a lightweight and simple library for converting
between native Clojure data structures and MessagePack byte formats.
clojure-msgpack only depends on Clojure itself; it has no third-party
dependencies.

## Installation

Get it from clojars: https://clojars.org/clojure-msgpack
![Clojars Project](http://clojars.org/clojure-msgpack/latest-version.svg)

## Usage

### Basic:
* ```pack```: Serialize object as a sequence of java.lang.Bytes.
* ```unpack``` Deserialize bytes as a Clojure object.
```clojure
(require '[msgpack.core :as msg])
(require 'msgpack.clojure-extensions)

(msg/pack {:compact true :schema 0})
; => #<byte[] [B@60280b2e>

(msg/unpack (msg/pack {:compact true :schema 0}))
; => {:schema 0, :compact true}
`````

### Streaming:
* ```unpack-stream```: Takes a [java.io.DataInput](http://docs.oracle.com/javase/7/docs/api/java/io/DataInput.html) as an argument. Usually you wrap this around an [InputStream](http://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html)
* ```pack-stream```: Takes a [java.io.DataOutput](http://docs.oracle.com/javase/7/docs/api/java/io/DataOutput.html) as an argument. Usually you wrap this around an [OutputStream](http://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html)
```clojure
(use 'clojure.java.io)
(import '(java.io.DataOutputStream) '(java.io.DataInputStream))

(with-open [o (output-stream "test.dat")]
  (let [data-output (java.io.DataOutputStream. o)]
    (msg/pack-stream {:compact true :schema 0} data-output)))

(with-open [i (input-stream "test.dat")]
  (let [data-input (java.io.DataInputStream. i)]
    (msg/unpack-stream data-input)))
; => {:schema 0, :compact true}
```

### Core types:

Clojure			    | MessagePack
----------------------------|------------
nil			    | nil
java.lang.Boolean	    | Boolean
java.lang.Float		    | Float
java.lang.Double	    | Float
java.math.BigDecimal	    | Float
java.lang.Number	    | Integer
java.lang.String	    | String
clojure.lang.Sequential	    | Array
clojure.lang.IPersistentMap | Map
msgpack.core.Ext	    | Extended

### Clojure Extended types:
Native Clojure types that don't have an obvious MessagePack counterpart are
given Extended types.

Clojure			    | MessagePack
----------------------------|------------
clojure.lang.Keyword	    | Extended (type = 3)
clojure.lang.Symbol	    | Extended (type = 4)
java.lang.Character	    | Extended (type = 5)
clojure.lang.Ratio	    | Extended (type = 6)
clojure.lang.IPersistentSet | Extended (type = 7)

To enable automatic conversion of these types, load the `clojure-extensions`
library.

With `msgpack.clojure-extensions`:
```clojure
(require 'msgpack.clojure-extensions)
(msg/pack :hello)
; => #<byte[] [B@a8c55bf>
```

Without `msgpack.clojure-extensions`:
```clojure
(msg/pack :hello)
; => IllegalArgumentException No implementation of method: :pack-stream of
; protocol: #'msgpack.core/Packable found for class: clojure.lang.Keyword
; clojure.core/-cache-protocol-fn (core _deftype.clj:544)
```

### Application Extended types:
You can also define your own Extended types with `extend-msgpack`.

```clojure
(require '[msgpack.macros :refer [extend-msgpack]])

(defrecord Person [name])

(extend-msgpack
  Person
  100
  [p] (.getBytes (:name p))
  [bytes] (->Person (String. bytes)))

(msg/unpack (msg/pack [(->Person "bob") 5 "test"]))
; => (#user.Person{:name "bob"} 5 "test")
```

## TODO
* Error checking
* Compatibility mode
* Benchmarks

## License
clojure-msgpack is MIT licensed. See the included LICENSE file for more details.
