/*
 * Jeopardy Game Desktop Application: game ui (grid of buttons) and player score keeper
 */

package jeopardy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GameBoard {
	private final String styles = "GUI_styles.css";	
	
	private final static String driverClass = "org.sqlite.JDBC";
	private final static String url = "jdbc:sqlite:jeopardyDB.sqlite";
	private Connection connection = null;
	private String query = null;
	
	private String currGame = "";
	private String[] catArray = new String[6];
	private static String[] QandAs = new String[6]; //q, a, ans-a ... ans-d
	private static int points;
	
	private final BorderPane root;
	private BorderPane right = new BorderPane();
	
	//build ui
	public GameBoard(String game) {		
		root = new BorderPane();
		root.setPadding(new Insets(0, 0, 0, 0));
		root.setId("gameNewRoot");
		
		GridPane gridPane = new GridPane();
		gridPane.setMinSize(0, 0);
		gridPane.setId("gameNewGrid");
		
		currGame = game;
		
		try {
			Class.forName(driverClass);
			connection = DriverManager.getConnection(url);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		//get category titles
		try {
			String[] tempArray = new String[30];
			
			query = "SELECT Category FROM " + currGame;
			PreparedStatement stmt = connection.prepareStatement(query);
			ResultSet results = stmt.executeQuery();
			
			int i = 0; 
	        while(results.next() && i < 30) {
	        	String string = results.getString("Category");
	        	
	        	if(!Arrays.asList(tempArray).contains(string))
	        		tempArray[i] = string;
	        	i++;
	        }
	        
	        catArray = Arrays.stream(tempArray).filter(Objects::nonNull).toArray(String[]::new);
	    } catch (SQLException sql) {
	    	System.out.println(sql);
	    }
		
		//add categories to grid
		for(int i = 0; i < 6; i++) {
			String title = catArray[i];
			Label cat = new Label(title);
			cat.setPadding(new Insets(3, 3, 3, 3));
			cat.setWrapText(true);
	        cat.setTextAlignment(TextAlignment.CENTER);
	        cat.setMaxWidth(Double.MAX_VALUE);
	        cat.setAlignment(Pos.CENTER);
	        cat.getStyleClass().add("categories");
	        gridPane.add(cat, i, 0);
		}
		
		//add questions to grid
		for(int i = 0; i < 6; i++) { //category
			for(int j = 0; j < 5; j++) { //question
				int dollar = 0;
				
				switch(j) {
					case 0:
						dollar = 200;
						break;
					case 1:
						dollar = 400;
						break;
					case 2:
						dollar = 600;
						break;
					case 3:
						dollar = 800;
						break;
					case 4:
						dollar = 1000;
						break;
					default: break;
				}
				
				Button btn = new Button("$"+dollar);
				btn.getStyleClass().add("gameBtns");
				btn.setOnAction(new ButtonListener());
				btn.setId("btn_"+i+"_"+dollar);
				gridPane.add(btn, i, j+1);
			}
		}    
	   
		//exit/back button
	    Button exitBtn = new Button("back");
		exitBtn.setId("exitBtn");
		exitBtn.setFocusTraversable(false);
		exitBtn.setCursor(Cursor.HAND);
		
		exitBtn.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	root.setPadding(new Insets(0, 0, 0, 0));
		        Login login = new Login();
		        root.setTop(null);
		        root.setRight(null);
		        root.setCenter(login.getLogin());
		    }
		});		
		
		VBox btns = new VBox(exitBtn);
		btns.setSpacing(10);
		btns.setAlignment(Pos.TOP_RIGHT);
		
		Player p = new Player();
		right.setTop(btns);
		right.setBottom(p.getPlayers());
		
	    right.setPadding(new Insets(10, 10, 10, 10));
	    
	    root.setCenter(gridPane);	
	    root.setRight(right);
	}
	
	//return ui
	public BorderPane getGameBoard() {		
        return root;
    }
	
	//set player's name from login
	public void setPlayerName(String n) {
		Player p = new Player();
		p.getPlayerName(n);
		right.setBottom(p.getPlayers());
	}
	
	//question buttons click opens Q/A popup
	private class ButtonListener implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {		
			Object src = event.getSource();
			((Node) src).setDisable(true);
			
			String category = "";
			String[] source = ((Node) src).getId().split("_");
			
			category = catArray[Integer.parseInt(source[1])];
			points = Integer.parseInt(source[2]);
	
			query = "SELECT * FROM " + currGame + " "
					+ "WHERE Category = '" + category + "' AND Points = '" + points + "'";
			PreparedStatement stmt;
			
			//get Q/A popup info
			try {
				stmt = connection.prepareStatement(query);
				ResultSet results = stmt.executeQuery();
				
				while(results.next()) {
					QandAs[0] = results.getString("Question");
					QandAs[1] = results.getString("Answer");
					QandAs[2] = results.getString("Ans_a");
					QandAs[3] = results.getString("Ans_b");
					QandAs[4] = results.getString("Ans_c");
					QandAs[5] = results.getString("Ans_d");
					
					System.out.println("Question: " + QandAs[0] + "\nAnswer: " + QandAs[1] + "\nA: " + QandAs[2] + "\nB: " + QandAs[3] + "\nC: " + QandAs[4] + "\nD: " + QandAs[5] + "\n");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			Stage stage = new Stage();
			stage.initStyle(StageStyle.UTILITY);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setWidth(550);
			stage.setHeight(400);
			
			Question q = new Question();
			Scene scene2 = new Scene(q.getQuestion());
			
			scene2.getStylesheets().add(styles);		

			stage.setScene(scene2);
			stage.setResizable(false);
			stage.show();	
		}			
	}
	
	public static String getQuestion() {
		return QandAs[0];
	}
	
	public static String getAnswer() {
		return QandAs[1];
	}
	
	public static String getANS_a() {
		return QandAs[2];
	}
	
	public static String getANS_b() {
		return QandAs[3];
	}
	
	public static String getANS_c() {
		return QandAs[4];
	}
	
	public static String getANS_d() {
		return QandAs[5];
	}
	
	public static int getPoints() {
		return points;
	}
		
}
