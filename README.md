# A Multi Function Data forwarding server

- To Use
    * When a client connects it must send a json format of the ClientInfo class in order to know information about the client
    * If client is host a new game will be created which any and all clients connected to that game will recieve any information that is sent
    * Multiple games per client is not yet supported
    * Commands to server consist of:
        - show /all -> shows all game Id that are running on the server
        - show clients "game" -> shows all clients within a certain game (password must be known and inserted in "game")
        - end /u "username" /p "password" -> ends the client with that username and password
        - remove "game" -> removes that game and ends every client within it

- How it words
     * A client connecting that is host will be put into its own game. It will have the password for that game that whoever joins with the same password and not host will join that game
     * Each game contains its own unique ID that is made from creation
     * Any info sent to the server from a game will be sent to everyone within that game
     * If no data is sent to server for 2 seconds it will send a ping to the client and if it gets either a pong or any type of data, then it will keep client. If nothing is given back for 5 seconds it will end client
     * Logs are made in the logs folder. With the ServerLog holding incoming clients and if they are accepted, while other Game logs are made under folder titled with that games id
     * Server is meant to keep running, so the commands were designed to be able to interact with the server while it is running
