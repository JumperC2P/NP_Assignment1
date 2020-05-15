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
