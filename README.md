# A Multi Function Data forwarding server

- To Use
    * When a client connects it must send a json format of the ClientInfo class in order to know information about the client
    * If client is host a new game will be created which any and all clients connected to that game will recieve any information that is sent
    * Multiple games per client is not yet supported
    * Commands to server consist of:
        - show /all -> shows all game Id that are running on the server
        - show clients "game" -> shows all clients within a certain game (password must be known and inserted in "game")
        