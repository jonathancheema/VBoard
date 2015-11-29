package client;

public class Game {
	private int gameID;
	private String host;
	private String gameTitle;
	private String sessionName;
	private int maxPlayers;
	private int currentPlayers = 1;
	private String password;
	private Object players[][];

	public Game(int gameID, String host, String gameTitle, String sessionName, int maxPlayers, String password, Player player) {
		this.gameID = gameID;
		this.host = host;
		this.gameTitle = gameTitle;
		this.sessionName = sessionName;
		this.maxPlayers = maxPlayers;
		this.password = password;

		this.players = new Object[maxPlayers][2];
		players[0][0] = host;
		players[0][1] = player;

		player.toPlayer(gameID + "");

	}

	public void sendMessage(String message) {
		for (int x = 0; x < maxPlayers; x++) {
			if (players[x][0] != null)
				((Player) players[x][1]).toPlayer(message);
		}
	}

	public void sendMove(String playerName, int mType, String message) {
		for (int x = 0; x < maxPlayers; x++) {
			if (players[x][0] != null && !players[x][0].equals(playerName))
				((Player) players[x][1]).toPlayer(playerName + ":" + mType + ":" + message);
		}
	}

	public void join(String playerName, String password, Player player) {
		if (password.equals(this.password)) {
			int x = 0;
			boolean joined = false;
			while (joined == false && x < maxPlayers) {
				if (players[x][0] == null) {
					joined = true;
					players[x][0] = playerName;
					players[x][1] = player;
					currentPlayers++;
					player.toPlayer(gameID + "");
					sendMessage("Server:" + "3:" + playerName + " has joined this game.");
				}
				x++;
			}
			if (joined == false)
				player.toPlayer("-2");
		} else
			player.toPlayer("-1");
	}

	public void leave(String message) {
		for (int x = 0; x < maxPlayers; x++) {
			if (players[x][0] != null)
				if (players[x][0].equals(message)) {
					players[x][0] = null;
					players[x][1] = null;
					currentPlayers--;
				}
		}
		if (currentPlayers == 0)
			Server.deleteGame(gameID);
		else
			sendMessage("Server:" + "3:" + message + " has left this game.");

	}

	public String getSessionName() {
		return sessionName;
	}

	public String getGameTitle() {
		return gameTitle;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getGameID() {
		return gameID;
	}

	public int getCurrentPlayers() {
		return currentPlayers;
	}

	public boolean hasPassword() {
		if (password.equals(""))
			return false;
		else
			return true;
	}
}
