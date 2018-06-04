# skocko-clj

Clojure game using mongodb.

## Prerequisites

1) You will need [Leiningen][] 2.0.0 or above installed.
2) You will need running mongodb. In file "skocko.properties" you 
    can set host, port and db-name.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start application, from command prompt point to application directory
 and run:

    lein ring server
    
Or if you prefer Docker, you can also point to application directory and run:
    
    docker-compose up
