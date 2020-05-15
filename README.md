# NP_Assignment1

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
> if you don't give the argument "demo", the server will start in production mode.

### Start Game Client
```bash
java client.GamePlayer demo 
```
> if you don't give the argument "demo", the client will start in production mode.

### East way to play:
1. execute **compile.sh**
2. execute **start_server.sh** to start a server (the server will start in production mode.)
3. execute **start_client.sh** to start a client (the server will start in production mode.)
