# NP_Assignment1

### Brief Introduction

The project is about a guessing game, which allows multiple players. The server will wait for 3 minutes (Production mode) or 10 seconds (Demo mode). In each round, the server will take the first three players in the lobby to play the game. Each player has 4 chances to guess the number between 0 and 12. During the gaming time, if a player doesn't response in 5 minutes (Procduction mode) or 30 seconds (Demo mode), the player will be quit the game. The gaming result will be shown until all players finish their game. After the game, players will be asked for whether to play again (p for playing game, q for quitting game). If the player wants to play again, he or she will be re-add the lobby and waits until server announces them to play. Also, players are allowed to exit the game during guessing, but once the player exits the game, his or her result will not be counted in.

### Package Introduction
- client:

The package contains all the client-side programs. `GamePlayer.java` is the main program to run and `ClientTimerTask.java` extends from `java.util.TimerTask` for catching the keep-alive message from server.

- server:

The package contains all the server-side programs. `GameServer.java` is the game server program. By running the java file, the game server will start to allow connections from clients. `PlayerHandler.java` is the main threading program. As one client connects to the server, it will be created as a new player **thread** and play the game in the thread. `PlayerResultHandler.java` is another thread used to handle the process of sending result message and asking for playing again or not. `ServerTimerTask.java` extends from `java.util.TimerTask` to send keep-alive message to client.

- tools:

The packages contains two utilities: 
1. `CalculateResult.java` : calculating gaming results and preparing result messages by the remaining chances.
2. `GameLogger.java` : a singleton object to generate a logger object which extends `java.util.Logger`

### Compilation Steps:
1. Move to src folder
2. execute the command line below to compile all the server-relate java files :
```bash
javac -sourcepath . server/GameServer.java
```
3. execute the command line below to compile all the server-relate java files :
```bash
javac -sourcepath . client/GamePlayer.java 
```

### Start Game Server
```bash
java server.GameServer demo 
```
> If you don't give the argument "demo", the server will start in production mode.

### Start Game Client
```bash
java client.GamePlayer demo 
```
> If you don't give the argument "demo", the client will start in production mode.

### East way to play:
1. execute **compile.sh** with the command `./compile.sh`
2. execute **start_server.sh** to start a server with the command `./start_server.sh`(the server will start in production mode.)
3. execute **start_client.sh** to start a client with the command `./start_client.sh`(the server will start in production mode.)
> If these three files cannot be executed, please run the command first `chmod +x compile.sh start_server.sh start_client.sh`.
