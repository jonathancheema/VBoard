package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Player extends Thread {

	private static final int NOTINGAME = -1;
	private static final int CREATE = 0;
	private static final int JOIN = 1;
	private static final int LEAVE = 2;
	private static final int MESSAGE = 3;
	private static final int MOVE = 4;
	private static final int GETBOARD = 5;

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	public Player(Socket socket) throws IOException {
		this.socket = socket;

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		log("New connection with player at " + socket);
	}

	public void run() {
		try {

			while (true) {
				String input = in.readLine();
				recieveMessage(input);
			}
		} catch (IOException e) {
			log("Error handling player: " + e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				log("Couldn't close a socket, what's going on?");
			}
			log("Connection with player closed");
		}
	}

	private void recieveMessage(String input) {
		if (input == null)
			return;
		if (input.equals("GETGAMES")) {
			sendGames();
		} else {
			String[] message = input.split(":", 4);
			int gameID = Integer.parseInt(message[0]);
			String playerName = message[1];
			int mType = Integer.parseInt(message[2]);
			log("Incoming Message: GameID: " + gameID + " MessageType: " + mType + " Message: " + message[3]);
			if (gameID == NOTINGAME) {
				if (mType == CREATE)
					// input:
					// NOTINGAME:playerName:CREATE:gameTitle:sessionName:maxPlayers:password
					createGame(playerName, message[3]);
				else if (mType == JOIN)
					// input: NOTINGAME:playerName:JOIN:gameID:password
					joinGame(playerName, message[3]);
			} else {
				if (mType == LEAVE)
					// input: gameID:playerName:LEAVE
					leaveGame(gameID, playerName);
				else if (mType == MESSAGE)
					// input: gameID:playerName:MESSAGE:...
					messageGame(gameID, playerName, mType, message[3]);
				else if (mType == MOVE)
					moveGame(gameID, playerName, mType, message[3]);
				else if (mType == GETBOARD) {
					System.out.println("Getting Board!!!");
					getBoard(gameID, playerName);
				}
			}
		}
	}

	private void sendGames() {
		Object[][] games = Server.getGames();
		int count = 0;
		for (int x = 0; x < games.length; x++)
			if (games[x][0] != null)
				count++;
		toPlayer(count + "");
		for (int x = 0; x < games.length; x++)
			if (games[x][0] != null)
				toPlayer(games[x][0] + ":" + games[x][1] + ":" + games[x][2] + ":" + games[x][3] + ":" + games[x][4] + ":" + games[x][5]);
	}

	public void toPlayer(String message) {
		log("Message to player: " + message);
		out.println(message);
	}

	// message: message
	private void messageGame(int gameID, String playerName, int mType, String message) {
		Server.games[gameID].sendMessage(playerName + ":" + mType + ":" + message);
	}

	private void moveGame(int gameID, String playerName, int mType, String message) {
		Server.games[gameID].sendMove(playerName, mType, message);
	}

	private void getBoard(int gameID, String playerName) {
		Server.games[gameID].getBoard(playerName);
	}

	// message: playerName
	private void leaveGame(int gameID, String playerName) {
		Server.games[gameID].leave(playerName);
	}

	// message: gameID:password
	private void joinGame(String playerName, String message) {
		String[] data = message.split(":", 2);
		Server.games[Integer.parseInt(data[0])].join(playerName, data[1], this);
	}

	// message: gameTitle:sessionName:maxPlayers:password
	private void createGame(String playerName, String message) {
		if (Server.validGames.isEmpty()) {
			out.println(NOTINGAME);
		} else {
			String[] data = message.split(":", 4);
			int gameID = Server.validGames.pop();
			Server.games[gameID] = new Game(gameID, playerName, data[0], data[1], Integer.parseInt(data[2]), data[3], this);
			log("Game Created: " + gameID + " : " + playerName + " : " + data[0] + " : " + data[1] + " : " + Integer.parseInt(data[2]) + " : " + data[3]);
		}
	}

	private void log(String log) {
		Server.log(log);
	}
}
