# Greater than Tetris
Client-server communication based on sockets and string commands without any high-level libruaries.

Libraries: JDK 17, JavaFX, Apache Derby and JUnit 5.

## Client-server application based on Sockets.
Server configuration should contain:
 - the number of users for game session (1 or 2),
 - timeout - session time after which the game will be automatically stopped.

Clients can play the game with/without opponent and view top 10 results.

Server generates one brick sequence for all clients, providing honest competition.
