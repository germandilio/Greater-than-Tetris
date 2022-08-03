# Greater than Tetris
Client-server communication based on sockets and string commands without Network high-level libraries.

Libraries: JDK 17, Lombok, JavaFX, Apache Derby and JUnit 5.

### Command API ###
[API docs](https://germandilio.github.io/Greater-than-Tetris/ru.hse.germandilio.tetris.tetris/ru/hse/germandilio/tetris/shared/commands/CommandsAPI.html)

### Screenshots ###

<image src=".images/Game_Two_Clients.png" alt="Dialog" style="height:300px; width:800px">
<image src=".images/Results_Two_Clients.png" alt="Dialog" style="height:300px; width:800px">
<image src=".images/Result_Single.png" alt="Dialog" style="height:480px; width:800px">
<image src=".images/Single.png" alt="Dialog" style="height:480px; width:800px">
<image src=".images/Top10.png" alt="Dialog" style="height:480px; width:800px">

## Client-server application based on Sockets.
Server configuration should contain:
 - the number of users for game session (1 or 2),
 - timeout - session time after which the game will be automatically stopped.

Clients can play the game with/without opponent and view top 10 results.

Server generates one brick sequence for all clients, providing honest competition.

## Gameplay ##
After registration, player can start the game.
(If you are playing with partner, you should waiting until he also started te game.)

### Core game mechanic ###
New generated brick will be presented in left down corner. You can drag & drop it to place on game board.

### Winner ###
You need to place more bricks on a board than your partner to be a winner. If equles, you need to finish you session in a shortest time.

