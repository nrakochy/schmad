# schmad
A schema adapter to convert a Clojure map to the desired schema implementation.

## Usage
See `examples` for a sample schema to use. Note: there are keys in the example map by design. It is not limited to any one schema implementation

#### Datomic
Use `schmad.datomic.adapter/generate-schema` to generate a lazy, Datomic-transactable seq, and the doc string on said method articulates the requisite params.

## To-do 
* `clojure-spec`
* `graphql` via `lacinia`

## License

Copyright © 2017 Nick Rakochy 

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
