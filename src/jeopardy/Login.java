/*
 * Jeopardy Game Desktop Application: login ui (fields for user name and game board, and log in button)
 */

package jeopardy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Login {	
	final String styles = "GUI_styles.css";		
	
	private final BorderPane root;
	private TextField name = new TextField();
	private ComboBox<String> gameType = new ComboBox<String>();
	private static ArrayList<String> gameTitles;
	
	//build ui
    public Login() {
    	try {
			setGameTitles();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	//build ui
		root = new BorderPane();
		root.setPadding(new Insets(0, 0, 0, 0));
		
		ImageView imgV = new ImageView(new Image(getClass().getResourceAsStream("bkgd.png")));
		imgV.setFitHeight(1080*0.7);
		imgV.setFitWidth(906*0.7);
		
		VBox designBox = new VBox(imgV);
		designBox.setAlignment(Pos.CENTER);
		designBox.setId("designBox");
		
		Text welcome = new Text("\n\nWELCOME TO JEOPARDY!\n");
		welcome.setId("welcomeMsg");
		
		GridPane gridPane = new GridPane();
	    gridPane.setVgap(12);
	    gridPane.setHgap(8);
		
		Text nameTxt = new Text("NAME: ");
		nameTxt.getStyleClass().add("promptTxt");
	    gridPane.add(nameTxt, 0, 0);
	    gridPane.add(name, 1, 0);
	    
	    Text levelTxt = new Text("GAME: ");
		levelTxt.getStyleClass().add("promptTxt");
		
		for(int i = 0; i < gameTitles.size(); i++) {
			gameType.getItems().add(gameTitles.get(i));
		}
		
		gameType.getSelectionModel().select(gameTitles.get(0));
	    gridPane.add(levelTxt, 0, 2);
	    gridPane.add(gameType, 1, 2);
		
		Button login = new Button("login");
		login.setOnAction(new LoginButtonListener());
		login.setId("loginBtn");
		
		VBox loginVBox = new VBox();
		loginVBox.getChildren().addAll(welcome, gridPane, login);	
		loginVBox.setAlignment(Pos.TOP_CENTER);
		loginVBox.setSpacing(12);
		loginVBox.setId("loginVBox");			
		
		SplitPane splitPane = new SplitPane();
		splitPane.getItems().addAll(designBox, loginVBox);
		splitPane.setDividerPositions(0.60);	
		
		designBox.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.60));
		loginVBox.maxWidthProperty().bind(splitPane.widthProperty().multiply(0.40));
		
		root.setCenter(splitPane);	
	}
    
    //return ui
    public BorderPane getLogin() {		
		return root;
    }
    
    //set gameTitles to names of db tables
	private static void setGameTitles() throws SQLException {
		ArrayList<String> a = new ArrayList<String>();
		
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		String url = "jdbc:sqlite:jeopardyDB.sqlite";
		
		try(Connection conn = DriverManager.getConnection(url);
		    ResultSet rs = conn.getMetaData().getTables(null, null, null, null)) {
		    
			while (rs.next()) {
				a.add(rs.getString("TABLE_NAME"));
		    }
		}
		
		gameTitles = a;
	}
	
	//check if username is valid and open editor for admin or game for all others
	private class LoginButtonListener implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			String nameInput = null;
			name.setStyle(null);
			
			//check if username is inputted
			if(name.getText() == null || name.getText().trim().isEmpty()) {
				name.setStyle("-fx-text-box-border: #ff3838;" + 
						"    -fx-control-inner-background: #fff0f0;" + 
						"    -fx-focus-color: #ff2020;" + 
						"    -fx-faint-focus-color: #ff202020;");
			} else {
				nameInput = name.getText(); 
				
				//open game board editor
				if(nameInput.equals("admin")) {
	        		root.setPadding(new Insets(0, 0, 0, 0));
	    		    EditGameboards edit = new EditGameboards();
	    		    root.setTop(null);
	    		    root.setCenter(edit.getEditPage());
	    		    System.out.println("update: game editor page\n");
	    		    
	    		//open game
	        	} else if(gameType.getValue() != null) {
	        		root.setCenter(null);
	        		
	        		GameBoard gameNT = new GameBoard(gameType.getValue());
	        		root.setCenter(gameNT.getGameBoard());
					gameNT.setPlayerName(name.getText());
					
					System.out.println("username: " + nameInput + "\n");
					System.out.println("update: login successful\ngame: " + gameType.getValue() + "\n");
	        	} else {
		        	System.out.println("error: unable to choose game");
	        	} 
			}				
		}
	}
	
}
