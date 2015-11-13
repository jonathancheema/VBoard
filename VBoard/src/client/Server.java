package client;

import java.net.ServerSocket;
import java.util.Stack;

public class Server {

	private static final int gameNumber = 50;
	static Stack<Integer> validGames;
	static Game[] games;

	public static void main(String[] args) throws Exception {
		System.out.println("The server is running.");
		// initialize game table
		games = new Game[gameNumber];
		validGames = new Stack<Integer>();
		for (int x = 0; x < gameNumber; x++)
			validGames.push(x);

		ServerSocket listener = new ServerSocket(9898);
		try {
			while (true) {
				new Player(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}

	static void log(String message) {
		System.out.println(message);
	}

	public static Object[][] getGames() {
		Object[][] results = new Object[gameNumber][6];
		for (int x = 0; x < gameNumber; x++) {
			if (games[x] != null) {
				results[x][0] = games[x].getGameID();
				results[x][1] = games[x].getSessionName();
				results[x][2] = games[x].getGameTitle();
				results[x][3] = games[x].getCurrentPlayers();
				results[x][4] = games[x].getMaxPlayers();
				results[x][5] = games[x].hasPassword();
			}
		}
		return results;
	}

	public static void deleteGame(int gameID) {
		games[gameID] = null;
		validGames.push(gameID);
	}
}