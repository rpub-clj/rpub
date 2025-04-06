# rPub

[![stability-alpha](https://img.shields.io/badge/stability-alpha-f4d03f.svg)](https://github.com/mkenney/software-guides/blob/master/STABILITY-BADGES.md#alpha)
[![Docker](https://img.shields.io/badge/docker-0.1.0-blue)](https://hub.docker.com/r/rpub/rpub)
[![Clojars](https://img.shields.io/badge/clojars-dev.rpub%2Frpub%200.1.0-blue)](https://clojars.org/dev.rpub/rpub)
[![Slack](https://img.shields.io/badge/slack-join_chat-orange.svg)](https://clojurians.slack.com/archives/C07QM1N21SP)

**A free open-source CMS written in Clojure.**

## Table of Contents

[Features](#features) • [Quick Start](#quick-start) • [API](#api) • [Plugins](#plugins) • [Contributing](#contributing) • [Credits](#credits) • [License](#license)

## Features

**[See the rPub announcement post for more details.](https://radsmith.com/rpub)**

- Designed to be easy to set up and use with no coding required
- Includes an admin UI to manage content and settings
- Supports custom fields to allow for all types of content
- Supports browsing and installing plugins from the admin UI
- Supports themes and customizing appearance from the admin UI
- Supports developing plugins and themes interactively using the REPL
- Uses SQLite by default with protocols to support alternative storage backends
- MIT license allows the code to be modified for both commercial and non-commercial use

<a href="https://raw.githubusercontent.com/rpub-clj/static-files/refs/heads/main/screenshot.webp"><img src="https://raw.githubusercontent.com/rpub-clj/static-files/refs/heads/main/screenshot.webp"></a>

## Quick Start

### App (With Docker)

*Requirements:* [Docker](https://docs.docker.com/get-started/introduction/get-docker-desktop/) • [Docker Compose](https://docs.docker.com/compose/install/)

```shell
mkdir rpub && cd rpub
curl -O https://raw.githubusercontent.com/rpub-clj/rpub/refs/tags/v0.1.0/docker-compose.yaml
docker compose up
```

### App (Without Docker)

*Requirements:* [Git](https://github.com/git-guides/install-git) • [Java](https://adoptium.net/installation/) • [Clojure](https://clojure.org/guides/install_clojure) • [Babashka](https://github.com/babashka/babashka#installation)

```shell
mkdir rpub && cd rpub
bb -Sdeps '{:deps {dev.rpub/rpub {:mvn/version "0.1.0"}}}' -m rpub.tasks/supervisor --mvn/version 0.1.0
```

### Library

*Requirements:* [Git](https://github.com/git-guides/install-git) • [Java](https://adoptium.net/installation/) • [Clojure](https://clojure.org/guides/install_clojure) • Auto-Restarts

*Note:* To load changes to the classpath outside of the REPL, rPub stops the JVM and expects it to be auto-restarted. To implement auto-restarts you can either a) use a wrapper (see [`rpub.tasks/supervisor`](https://github.com/rpub-clj/rpub/blob/main/API.md#rpub.tasks/supervisor)) or b) configure an existing supervisor to do this (e.g. Docker or systemd).

```clojure
;; deps.edn
{:paths ["src"]
 :deps {dev.rpub/rpub {:mvn/version "0.1.0"}}}

;; src/com/example.clj
(ns com.example
  (:require [rpub.core :as rpub])

(defn -main [& _]
  (rpub/start!))
```

```shell
clojure -M -m com.example
```

## API

**[See the `API.md` file for all public functions.](API.md)**

*Note: Any functions not listed in this file should be considered implementation details and subject to change.*

## Plugins

[**See the `rpub-clj/plugins` repository for a list of all available plugins.**](https://github.com/rpub-clj/plugins)

Plugins can be added by extending the [`rpub.core/plugin`](https://github.com/rpub-clj/rpub/blob/main/API.md#rpub.core/plugin) multimethod:

```clojure
;; data/example-plugin/deps.edn
{:paths ["src"]
 :deps {}
 :aliases {:dev {:extra-deps {dev.rpub/rpub {:mvn/version "0.1.0"}}}}}

;; data/example-plugin/src/com/example/plugin.clj
(ns com.example.plugin
  (:require [rpub.core :as rpub]))

(defn init [opts])

(defn middleware [opts])

(defn routes [opts])

(defmethod rpub/plugin ::plugin [_]
  {:label "Hello"
   :description "An example plugin."
   :init init
   :middleware middleware
   :routes routes})
```

To use the plugin, require the namespace before you start rPub. If you install a plugin through the admin UI, it will do this for you automatically:

```clojure
;; data/deps.edn
{:paths ["."]
 :deps {dev.rpub/rpub {:mvn/version "0.1.0"}
        com.example/plugin {:local/root "example-plugin"}}}

;; data/app.clj
(ns app
  (:require [rpub.main :as main]
            [com.example.plugin]))

(defn -main [& _]
  (main/start!))
```

## Contributing

If you'd like to contribute to rPub, you're welcome to create [issues for ideas, feature requests, and bug reports](https://github.com/rpub-clj/rpub/issues).

## Credits

See the [`CREDITS.md`](CREDITS.md) file.

## License

Copyright © 2025 Radford Smith

rPub is distributed under the [MIT License](LICENSE).
