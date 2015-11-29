package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	private static final int CREATE = 0;
	private static final int JOIN = 1;
	private static final int LEAVE = 2;
	private static final int MESSAGE = 3;
	private static final int MOVE = 4;
	private static final int GETBOARD = 5;

	private static BufferedReader in;
	private static PrintWriter out;

	private static Socket socket;

	private static String userName;
	private static String serverAddress = "localhost";
	private static int gameID = -1;

	public Client() throws IOException {
	}

	public void close() throws IOException {
		socket.close();
	}

	public void connectToServer() throws IOException {

		socket = new Socket(serverAddress, 9898);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	public void setUserName(String newName) {
		userName = newName;
	}

	public void setServerAddress(String address) {
		serverAddress = address;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public int getGameID() {
		return gameID;
	}

	public int createGame(String gameTitle, String sessionName, int maxPlayers, String password) throws IOException {
		// NOTINGAME:playerName:CREATE:gameTitle:sessionName:maxPlayers:password
		out.println(gameID + ":" + userName + ":" + CREATE + ":" + gameTitle + ":" + sessionName + ":" + maxPlayers + ":" + password);
		int result = Integer.parseInt(in.readLine());
		if (result >= 0)
			gameID = result;
		return result;
	}

	public int joinGame(int joiningGameID, String password) throws IOException {
		// NOTINGAME:playerName:JOIN:gameID:password
		out.println(gameID + ":" + userName + ":" + JOIN + ":" + joiningGameID + ":" + password);
		int result = Integer.parseInt(in.readLine());
		if (result >= 0)
			gameID = result;
		return result;
	}

	public void leaveGame() throws IOException {
		// input: gameID:playerName:LEAVE
		out.println(gameID + ":" + userName + ":" + LEAVE + ":-1");
	}

	public void messageGame(String message) {
		// input: gameID:playerName:MESSAGE:...
		out.println(gameID + ":" + userName + ":" + MESSAGE + ":" + message);
	}

	public void moveGame(String moveData) {
		// input: gameID:playerName:MESSAGE:...
		out.println(gameID + ":" + userName + ":" + MOVE + ":" + moveData);
	}

	public String[] getGames() throws IOException {
		out.println("GETGAMES");
		int count = Integer.parseInt(in.readLine());
		String[] result = new String[count];
		for (int x = 0; x < count; x++) {
			result[x] = in.readLine();
		}
		return result;
	}

	public String getNext() throws IOException {
		return in.readLine();
	}

	public void getBoard() throws IOException {
		out.println(gameID + ":" + userName + ":" + GETBOARD + ":BLANK");
	}

	public void setGameID(int i) {
		gameID = i;
	}

	public String getUserName() {
		return userName;
	}
}