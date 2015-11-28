package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FXMLController {

	private Client client;

	private ServerHandler thread;

	@FXML
	private Text loginWarning;

	@FXML
	private Button loginQuitButton;

	@FXML
	private StackPane loginBox;
	@FXML
	private StackPane configureBox;
	@FXML
	private TextField userName;

	@FXML
	private StackPane choiceBox;

	@FXML
	StackPane createBox;
	@FXML
	StackPane joinBox;
	@FXML
	private ChoiceBox<String> createGame;
	@FXML
	private ChoiceBox<String> createMaxPlayers;
	ObservableList<String> maxPlayersList = FXCollections.observableArrayList("2", "3", "4");

	@FXML
	private HBox gameBox;

	@FXML
	private ImageView gamePiece_1;

	@FXML
	private ImageView gamePiece_2;

	@FXML
	private ImageView gamePiece_3;

	double orgSceneX, orgSceneY;
	double orgTranslateX, orgTranslateY;

	@FXML
	protected void loginQuitButtonAction(ActionEvent event) {
		Stage stage = (Stage) loginQuitButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	protected void loginConfigureButtonAction(ActionEvent event) {
		configureBox.setVisible(true);
		loginBox.setVisible(false);
	}

	@FXML
	protected void loginLoginButtonAction(ActionEvent event) throws IOException {
		if (userName.getText().matches("^[a-zA-Z0-9_]+$")) {
			client.setUserName(userName.getText());
			client.connectToServer();
			loginBox.setVisible(false);
			choiceBox.setVisible(true);
		} else {
			loginWarning.setText("Alphanumeric and underscores only.");
		}
	}

	@FXML
	private TextField configureHostAddress;

	@FXML
	protected void configureDoneButtonAction(ActionEvent event) throws IOException {
		client.setServerAddress(configureHostAddress.getText());
		loginBox.setVisible(true);
		configureBox.setVisible(false);
	}

	@FXML
	protected void choiceBackButtonAction(ActionEvent event) throws IOException {
		client.close();
		choiceBox.setVisible(false);
		loginBox.setVisible(true);
	}

	@FXML
	protected void choiceCreateButtonAction(ActionEvent event) throws IOException {
		choiceBox.setVisible(false);
		createBox.setVisible(true);
	}

	@FXML
	private TableView<GameData> joinTable;

	@SuppressWarnings("unchecked")
	@FXML
	protected void choiceJoinButtonAction(ActionEvent event) throws IOException {
		TableColumn<GameData, String> gameIDCol = new TableColumn<GameData, String>("GameID");
		gameIDCol.setCellValueFactory(new PropertyValueFactory<GameData, String>("gameID"));
		gameIDCol.setPrefWidth(58);
		gameIDCol.setResizable(false);

		TableColumn<GameData, String> passwordCol = new TableColumn<GameData, String>("Password");
		passwordCol.setCellValueFactory(new PropertyValueFactory<GameData, String>("password"));
		passwordCol.setPrefWidth(60);
		passwordCol.setResizable(false);

		TableColumn<GameData, String> sessionNameCol = new TableColumn<GameData, String>("Session Name");
		sessionNameCol.setCellValueFactory(new PropertyValueFactory<GameData, String>("session"));
		sessionNameCol.setPrefWidth(175);
		sessionNameCol.setResizable(false);

		TableColumn<GameData, String> gameCol = new TableColumn<GameData, String>("Game");
		gameCol.setCellValueFactory(new PropertyValueFactory<GameData, String>("game"));
		gameCol.setPrefWidth(175);
		gameCol.setResizable(false);

		TableColumn<GameData, String> playersCol = new TableColumn<GameData, String>("Players");
		playersCol.setCellValueFactory(new PropertyValueFactory<GameData, String>("players"));
		playersCol.setPrefWidth(60);
		playersCol.setResizable(false);

		ObservableList<GameData> gameList = FXCollections.observableArrayList();

		String[] games = client.getGames();
		for (int x = 0; x < games.length; x++) {
			String name = games[x].split(":", 6)[2];
			if (gamesList.contains(name))
				gameList.add(new GameData(games[x]));
		}
		joinTable.setItems(gameList);
		joinTable.getColumns().addAll(gameIDCol, passwordCol, sessionNameCol, gameCol, playersCol);

		joinBox.setVisible(true);
		choiceBox.setVisible(false);
	}

	public static class GameData {
		private final String gameID;
		private final String password;
		private final String session;
		private final String game;
		private final String players;

		private GameData(String data) {
			String[] m = data.split(":", 6);
			this.gameID = m[0];
			this.password = m[5];
			this.session = m[1];
			this.game = m[2];
			this.players = m[3] + "/" + m[4];
		}

		public String getGameID() {
			return gameID;
		}

		public String getPassword() {
			return password;
		}

		public String getSession() {
			return session;
		}

		public String getGame() {
			return game;
		}

		public String getPlayers() {
			return players;
		}
	}

	@FXML
	protected void createBackButtonAction(ActionEvent event) throws IOException {
		createBox.setVisible(false);
		choiceBox.setVisible(true);
	}

	@FXML
	private TextField createSessionName;

	@FXML
	private CheckBox createPasswordBox;
	@FXML
	private TextField createPassword;
	@FXML
	private Button createStartButton;
	@FXML
	private Text createWarning;

	@FXML
	protected void createStartButtonAction(ActionEvent event) throws IOException {
		String gameName = createGame.getValue();
		String sessionName = createSessionName.getText();
		String maxPlayers = createMaxPlayers.getValue();
		String password = "";

		if (createGame.getValue() == null) {
			createWarning.setText("Please pick a game.");
		} else if (!createGame.getValue().matches("^[a-zA-Z0-9_]+$")) {
			createWarning.setText("Game name can only contain a-zA-Z0-9_.");
		} else if (!sessionName.matches("^[a-zA-Z0-9_]+$")) {
			createWarning.setText("Session name can only contain a-zA-Z0-9_.");
		} else if (createPasswordBox.isSelected() && !createPassword.getText().matches("^[a-zA-Z0-9_]+$")) {
			createWarning.setText("Password can only contain a-zA-Z0-9_.");
		} else {
			if (createPasswordBox.isSelected())
				password = createPassword.getText();
			client.createGame(gameName, sessionName, Integer.parseInt(maxPlayers), password);
			createBox.setVisible(false);

			setUpGamePieces(gameName);

			gameBox.setVisible(true);
			thread.start();
		}
	}

	private void setUpGamePieces(String gameName) {
		File gameDir = new File("games/" + gameName);
		// File[] gameFolders = gameDir.listFiles(new FileFilter() {
		// public boolean accept(File pathname) {
		// return pathname.isDirectory();
		// }
		// });

		if (gameDir == null) {
			System.out.println("gameFolders was null");
		} else {
			String jsonData = readFile("games/" + gameName + "/loadInstructions.json");
			JSONObject jobj = new JSONObject(jsonData);
			JSONArray jarr = new JSONArray(jobj.getJSONArray("stack").toString());
			System.out.println("board to use: " + jobj.getString("board"));

			String gameImages = jarr.getString(0);
			File testDir = new File("games/" + gameName + "/" + gameImages);
			String[] images = testDir.list();
			for (int i = 0; i < images.length; i++) {
				// System.out.println("Stacking:
				// "+"games/"+gameName+"/"+gameImages+"/"+images[i]);
				Image temp = new Image("File:games/" + gameName + "/" + gameImages + "/" + images[i]);
				ImageView tempImageView = new ImageView(temp);
				tempImageView.setId("image_" + i);
				tempImageView.setOnMousePressed(imageOnMousePressedEventHandler);
				tempImageView.setOnMouseReleased(imageOnMouseReleasedEventHandler);
				tempImageView.setOnMouseDragged(imageOnMouseDraggedEventHandler);
				this.gameBox.getChildren().add(tempImageView);
			}
		}

		// File[] gameImages = gameFolders[1].listFiles();
		// Image gp1 = new Image("File:" + gameImages[0] + "");
		// Image gp2 = new Image("File:" + gameImages[1] + "");
		// Image gp3 = new Image("File:" + gameImages[2] + "");
		// this.gamePiece_1.setImage(gp1);
		// this.gamePiece_2.setImage(gp2);
		// this.gamePiece_3.setImage(gp3);
		//
		// gamePiece_1.setOnMousePressed(imageOnMousePressedEventHandler);
		// gamePiece_1.setOnMouseReleased(imageOnMouseReleasedEventHandler);
		// gamePiece_1.setOnMouseDragged(imageOnMouseDraggedEventHandler);
		// gamePiece_2.setOnMousePressed(imageOnMousePressedEventHandler);
		// gamePiece_2.setOnMouseReleased(imageOnMouseReleasedEventHandler);
		// gamePiece_2.setOnMouseDragged(imageOnMouseDraggedEventHandler);
		// gamePiece_3.setOnMousePressed(imageOnMousePressedEventHandler);
		// gamePiece_3.setOnMouseReleased(imageOnMouseReleasedEventHandler);
		// gamePiece_3.setOnMouseDragged(imageOnMouseDraggedEventHandler);

	}

	private String readFile(String filename) {
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	EventHandler<MouseEvent> imageOnMousePressedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			orgSceneX = t.getSceneX();
			orgSceneY = t.getSceneY();
			orgTranslateX = ((Node) (t.getSource())).getTranslateX();
			orgTranslateY = ((Node) (t.getSource())).getTranslateY();
		}
	};

	EventHandler<MouseEvent> imageOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			double offsetX = t.getSceneX() - orgSceneX;
			double offsetY = t.getSceneY() - orgSceneY;
			double newTranslateX = orgTranslateX + offsetX;
			double newTranslateY = orgTranslateY + offsetY;
			// String pieceID = ((Node) t.getSource()).getId();
			((Node) (t.getSource())).setTranslateX(newTranslateX);
			((Node) (t.getSource())).setTranslateY(newTranslateY);
		}
	};

	EventHandler<MouseEvent> imageOnMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {
			double offsetX = t.getSceneX() - orgSceneX;
			double offsetY = t.getSceneY() - orgSceneY;
			double newTranslateX = orgTranslateX + offsetX;
			double newTranslateY = orgTranslateY + offsetY;
			String pieceID = ((Node) t.getSource()).getId();
			((Node) (t.getSource())).setTranslateX(newTranslateX);
			((Node) (t.getSource())).setTranslateY(newTranslateY);

			// METHOD TO SEND COORDINATES TO SERVER HERE.
			client.moveGame(pieceID + ":" + newTranslateX + ":" + newTranslateY);
		}
	};

	@FXML
	private TextField joinPassword;
	@FXML
	private Text joinWarning;

	@FXML
	protected void joinJoinButtonAction(ActionEvent event) throws IOException {
		if (joinTable.getSelectionModel().getSelectedItem() != null) {
			String password = joinPassword.getText();
			GameData gameData = joinTable.getSelectionModel().getSelectedItem();
			int gameID = Integer.parseInt(gameData.getGameID());
			if (gameData.getPassword().equals("false"))
				password = "";
			if (client.joinGame(gameID, password) >= 0) {
				joinBox.setVisible(false);
				setUpGamePieces(gameData.getGame().toString());
				gameBox.setVisible(true);
				thread.start();
			} else {
				joinWarning.setText("Incorrect password.");
			}
		} else {
			joinWarning.setText("Select a game to join.");
		}
	}

	@FXML
	protected void joinBackButtonAction(ActionEvent event) throws IOException {
		joinBox.setVisible(false);
		choiceBox.setVisible(true);
	}

	@FXML
	private TextField gameChatField;

	@FXML
	private TextArea gameTextArea;

	private void gameUpdateChat(String string) {
		gameTextArea.appendText(string + "\n");
	}

	@FXML
	protected void gameSendButtonAction(ActionEvent event) throws IOException {
		client.messageGame(gameChatField.getText());
		gameChatField.setText("");
	}

	@FXML
	protected void gameLeaveGameButtonAction(ActionEvent event) throws IOException, InterruptedException {
		thread.stopThread();

		client.leaveGame();
		client.close();

		String name = client.getUserName();
		client = new Client();
		client.setUserName(name);
		client.connectToServer();
		client.setGameID(-1);
		thread = new ServerHandler();

		gameBox.setVisible(false);
		choiceBox.setVisible(true);
	}

	private ObservableList<String> gamesList = null;

	@FXML
	private void initialize() throws IOException, InterruptedException {
		client = new Client();
		configureHostAddress.setText(client.getServerAddress());

		joinTable.setPlaceholder(new Label("No games available."));

		thread = new ServerHandler();

		gamesList = FXCollections.observableArrayList();
		File dir = new File("games");

		File[] subDirs = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		for (File subDir : subDirs) {
			gamesList.add(subDir.getName().replaceAll("_", " "));
		}
		createGame.setItems(gamesList);

		createMaxPlayers.setValue("2");
		createMaxPlayers.setItems(maxPlayersList);
	}

	private class ServerHandler extends Thread {

		private volatile boolean running = true;

		@Override
		public void run() {
			try {
				String response;
				while (running) {
					response = client.getNext();
					String[] data = response.split(":", 3);
					String userName = data[0];
					int type = Integer.parseInt(data[1]);
					String message = data[2];
					if (type == 3) {
						gameUpdateChat(userName + ": " + message);
					} else if (type == 4) {
						String[] coordinates = message.split(":", 3);

						gameBox.lookup("#" + coordinates[0].toString()).setTranslateX(Double.parseDouble(coordinates[1]));
						gameBox.lookup("#" + coordinates[0].toString()).setTranslateY(Double.parseDouble(coordinates[2]));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void stopThread() throws InterruptedException {
			running = false;
		}
	}
}
