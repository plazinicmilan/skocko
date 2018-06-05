# skocko-clj

This application is a game, based on popular serbian tv show Slagalica. It is written in Clojure programming language and uses mongodb.

### Game roles:

1) When you start the game (button New game), 4-symbol random combination has been made, and you have to guess that combination.
2) You can insert and remove symbols from table, but when you insert fourth symbol in a row, your combination is being calculated and you cannot change it anymore.
3) Calculation results meaning:
	* red circle - one symbol is in combination and is in the right place
	* yellow circle - one symbol is in combination but not in right place
	* black circle - one symbol is not in combination at all
4) You have 6 tries until the end of the game
5) After you finish the game (weather you guess combination or you try 6 times) you will get pop-up window to insert your name (it is mandatory). If you don't want to save game info to database, you can close window or just click beside window and new game will start.

Also, from home page, you have an option to see results (button Show results), then remove them (button Delete) or see game details (button Show)

## Prerequisites (Unless you use Docker)

1) You will need [Leiningen][] installed.
2) You will need running mongodb. In file "skocko.properties" you 
    can set host, port where mongodb is running and db (db - database name, and if you don't have it created, it will create automatically).

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start application, from command prompt point to application directory
 and run:

    lein ring server
    
Or if you prefer Docker you can also point to application directory and just run: 
    
    docker-compose up
