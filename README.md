# jte-clj

A Clojure templating library - wrapper around [JTE](https://jte.gg/).

## Usage

### Engine
The first thing you want to do, is to construct a `gg.jte.TemplateEngine` object.
You do this via the `engine/create` fn (or `engine/create-precompiled` but more on this later).
It takes a Path/File/URL/URI (i.e. where the .jte templates are located), and optionally
a content-type - `:template/plain` (default) VS `:template/html`. This is a thread-safe object, 
expected to be long-lived. 

Example: 
```clj
(def engine-plain
  (-> (io/resource "jte-templates") 
      (engine/create :template/plain)))
```

### Rendering
This is achieved (mainly) via the `template/render!` fn. It takes the following arguments:

1. the engine (constructed earlier)
2. the file-name (String) of the template to render (must exist in the directory declared when constructed the engine)
3. the parameters (Map) to 'slot-into' the template (the template refers to those by their key in this map)
4. the rendering output (i.e. where to render the result) - should be `String`/`Path`/`File`/`Writer`/`PrintWriter`

Specifically for rendering to String, there is a convenience fn `template/render-to-string`, 
which returns an actual String.

Example: 

Given a `resources/jte-templates/hello.jte` file containing:

```
@param String name
@param String city

Hello ${city} - my name is ${name}!
```
We can render it like so:

```clj
(def params {:name "dimitris" :city "Manchester"})
(template/render-to-string engine-plain "hello.jte" params "")

;; => "\nHello Manchester - my name is dimitris!"
```

### Pre-compiling
An engine constructed via `engine/create` compiles the template to java 
on-the-fly, which adds a certain overhead. However, you can do that ahead of time,
and leverage `engine/create-precompiled`. The only difference here is that it expects 
a directory to find compiled java (i.e. `.class`) files - NOT `.jte` ones! Therefore,
you need to turn your `.jte` files to `.class` ones. In Java, this is typically done via 
a maven/gradle plugin, but in Clojure, the easiest way is to generate the Java sources 
(i.e. `.java` files) straight into your project sources, and let your build tool 
(e.g. `lein`, `tools.deps` etc) do the rest (i.e. to compile them as usual Java sources).

You can turn your `.jte` files to `.java` ones via the `engine/generate-sources` fn.
It returns a vector of the generated file paths (under `:target-dir`).

Example: 

```clj
(engine/generate-sources 
  {:source-dir   (io/resource "jte-templates")
   :target-dir   "src/java"
   :package-name "foo.templates"})

;; => ["foo/templates/JtehelloGenerated.java"]                   
```
Once you have done that, and assuming your build tool will AOT java sources,
you can create your engine like so:

```clj
(def engine-plain-precompiled
  (engine/create "foo/templates" :template/plain))
```

## License

Copyright © 2025 Dimitrios Piliouras

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
