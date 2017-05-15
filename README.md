# schmad
A schema adapter to convert a Clojure map to the desired schema implementation.

## Usage
See `examples` for a sample schema to use. Note: there are extra keys in the example map by design. It is not limited to any one schema implementation.

To use: 
* Require `[schmad.adapter]` 
* `(get-schema :named-implementation data-map)` to generate a lazy seq with the desired data type. 
  - E.g. `(get-schema :datomic m)`
  - Currently renders for the following: `:datomic`, `:lacinia`, and `:postgresql` 

## License

Copyright © 2017 Nick Rakochy 

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
