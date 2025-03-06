# Table of contents
-  [`rpub.core`](#rpub.core)  - The core API for using rPub as a library.
    -  [`defaults`](#rpub.core/defaults) - The default options for the rPub server.
    -  [`get-content-items`](#rpub.core/get-content-items) - Get a sequence of content items.
    -  [`get-content-types`](#rpub.core/get-content-types) - Get a sequence of content types.
    -  [`get-settings`](#rpub.core/get-settings) - Get a sequence of settings.
    -  [`get-users`](#rpub.core/get-users) - Get a sequence of users.
    -  [`plugin`](#rpub.core/plugin) - A multimethod that returns the plugin definition for key k.
    -  [`start!`](#rpub.core/start!) - Start the rPub server.
    -  [`stop!`](#rpub.core/stop!) - Stop the rPub server.
    -  [`url-for`](#rpub.core/url-for) - Get a URL for a content item.
-  [`rpub.main`](#rpub.main)  - The main entry point for using rPub as an application.
    -  [`current-system`](#rpub.main/current-system) - An atom containing the current rPub server and REPL processes.
    -  [`repl-defaults`](#rpub.main/repl-defaults) - The default options for the REPL.
    -  [`start!`](#rpub.main/start!) - Start the rPub server and an optional REPL.
    -  [`stop!`](#rpub.main/stop!) - Stop the rPub server but not the REPL.
-  [`rpub.tasks`](#rpub.tasks)  - Tasks for rPub projects (compatible with Babashka).
    -  [`supervisor`](#rpub.tasks/supervisor) - Starts a supervisor that auto-restarts an rPub server.

-----
# <a name="rpub.core">rpub.core</a>


The core API for using rPub as a library.




## <a name="rpub.core/defaults">`defaults`</a> [:page_facing_up:](/blob/main/src/rpub/core.clj#L234-L241)
<a name="rpub.core/defaults"></a>

The default options for the rPub server.

## <a name="rpub.core/get-content-items">`get-content-items`</a> [:page_facing_up:](/blob/main/src/rpub/core.clj#L150-L154)
<a name="rpub.core/get-content-items"></a>
``` clojure

(get-content-items model opts)
```


Get a sequence of content items.

## <a name="rpub.core/get-content-types">`get-content-types`</a> [:page_facing_up:](/blob/main/src/rpub/core.clj#L144-L148)
<a name="rpub.core/get-content-types"></a>
``` clojure

(get-content-types model opts)
```


Get a sequence of content types.

## <a name="rpub.core/get-settings">`get-settings`</a> [:page_facing_up:](/blob/main/src/rpub/core.clj#L129-L132)
<a name="rpub.core/get-settings"></a>
``` clojure

(get-settings model opts)
```


Get a sequence of settings.

## <a name="rpub.core/get-users">`get-users`</a> [:page_facing_up:](/blob/main/src/rpub/core.clj#L134-L137)
<a name="rpub.core/get-users"></a>
``` clojure

(get-users model opts)
```


Get a sequence of users.

## <a name="rpub.core/plugin">`plugin`</a> [:page_facing_up:](/blob/main/src/rpub/core.clj#L23-L34)
<a name="rpub.core/plugin"></a>

A multimethod that returns the plugin definition for key k.

  Each method should return a map with the following keys:

  :label - (required) a label
  :description - (required) a description
  :init - (optional) an initialization function
  :middleware - (optional) a sequence of middleware functions
  :routes - (optional) a sequence of route definitions

## <a name="rpub.core/start!">`start!`</a> [:page_facing_up:](/blob/main/src/rpub/core.clj#L247-L254)
<a name="rpub.core/start!"></a>
``` clojure

(start! & {:as opts})
```


Start the rPub server.

## <a name="rpub.core/stop!">`stop!`</a> [:page_facing_up:](/blob/main/src/rpub/core.clj#L256-L259)
<a name="rpub.core/stop!"></a>
``` clojure

(stop! system)
```


Stop the rPub server.

## <a name="rpub.core/url-for">`url-for`</a> [:page_facing_up:](/blob/main/src/rpub/core.clj#L139-L142)
<a name="rpub.core/url-for"></a>
``` clojure

(url-for content-item req)
```


Get a URL for a content item.

-----
# <a name="rpub.main">rpub.main</a>


The main entry point for using rPub as an application.




## <a name="rpub.main/current-system">`current-system`</a> [:page_facing_up:](/blob/main/src/rpub/main.clj#L37-L40)
<a name="rpub.main/current-system"></a>

An atom containing the current rPub server and REPL processes.

## <a name="rpub.main/repl-defaults">`repl-defaults`</a> [:page_facing_up:](/blob/main/src/rpub/main.clj#L52-L56)
<a name="rpub.main/repl-defaults"></a>

The default options for the REPL.

## <a name="rpub.main/start!">`start!`</a> [:page_facing_up:](/blob/main/src/rpub/main.clj#L58-L69)
<a name="rpub.main/start!"></a>
``` clojure

(start! & {:as opts})
```


Start the rPub server and an optional REPL.

  The REPL is enabled by default (see [`rpub.main/repl-defaults`](#rpub.main/repl-defaults)).

## <a name="rpub.main/stop!">`stop!`</a> [:page_facing_up:](/blob/main/src/rpub/main.clj#L71-L76)
<a name="rpub.main/stop!"></a>
``` clojure

(stop!)
```


Stop the rPub server but not the REPL.

-----
# <a name="rpub.tasks">rpub.tasks</a>


Tasks for rPub projects (compatible with Babashka).




## <a name="rpub.tasks/supervisor">`supervisor`</a> [:page_facing_up:](/blob/main/src/rpub/tasks.clj#L31-L52)
<a name="rpub.tasks/supervisor"></a>
``` clojure

(supervisor & {:as opts})
```


Starts a supervisor that auto-restarts an rPub server.

  The supervisor starts a child rPub server using the `clojure` CLI command and
  restarts it automatically if it stops with exit code 0. This allows rPub to
  load new Clojure dependencies after modifying the `deps.edn` file through the
  admin UI.

  This function is meant to be run using Babashka to minimize memory overhead.
