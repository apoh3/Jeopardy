/*
 * Jeopardy Game Desktop Application: for admin, editing db ui
 */

package jeopardy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class EditGameboards {
	private final BorderPane root;
	
	private ComboBox<String> gameType = new ComboBox<String>();
	private TabPane tabPane = new TabPane();
	private static ArrayList<String> gameTitles;
	
	//build ui
	public EditGameboards() {
		try {
			setGameTitles();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		root = new BorderPane();	
		root.setPadding(new Insets(10, 10, 10, 10));
		root.setId("editPage");
	    
		Label label = new Label("Game Editor");
		label.setId("editLabel");
		
		Button saveBtn = new Button("save");
		saveBtn.setId("saveBtn");
		saveBtn.setVisible(false);
		saveBtn.setFocusTraversable(false);
		saveBtn.setOnAction(new SaveButtonListener());
		
		Button exitBtn = new Button("cancel");
		exitBtn.setId("exitBtn");
		exitBtn.setVisible(false);
		exitBtn.setFocusTraversable(false);
		
		exitBtn.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	root.setPadding(new Insets(0, 0, 0, 0));
    		    EditGameboards edit = new EditGameboards();
    		    root.setTop(null);
    		    root.setCenter(edit.getEditPage());
    		    System.out.println("update: game editor page\n");
		    }
		});
		
		HBox btns = new HBox(exitBtn, saveBtn);
		btns.setSpacing(8);
		
		BorderPane miniBP = new BorderPane();
		miniBP.setLeft(label);
		miniBP.setRight(btns);
		
		gameType.setPromptText("select game");
		
		for(int i = 0; i < gameTitles.size(); i++) {
			gameType.getItems().add(gameTitles.get(i));
		}
		
		gameType.setId("gameEdit");
		
		HBox mid = new HBox(gameType);
		mid.setAlignment(Pos.CENTER);
		VBox top = new VBox(miniBP, mid);
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setId("editSP");
		
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		scrollPane.setContent(tabPane);
		tabPane.prefWidthProperty().bind(root.widthProperty().subtract(40));	
		
	    gameType.valueProperty().addListener(new ChangeListener<String>() {
	        @Override public void changed(@SuppressWarnings("rawtypes") ObservableValue ov, String t, String t1) {
    			root.setCenter(scrollPane);	
    			top.getChildren().remove(mid);
    			
    			label.setText("Game Editor: " + gameType.getValue());
    			saveBtn.setVisible(true);
    			exitBtn.setVisible(true);
    			
    			for(int i = 0; i < 6; i++) {
    				Tab tab = new Tab(" Category " + (i+1));
    				tab.setContent(getGrid(i, gameType.getValue()));
    				tabPane.getTabs().add(tab);
    			}
    			
    			System.out.println("update: edit " + gameType.getValue() + "\n");	    			
    		}    
	    });
	    
		root.setTop(top);
	}
	
	//return ui
	public BorderPane getEditPage() {
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
	
	//print current db info into textfields in grid
	public GridPane getGrid(int tab, String table) {		
		GridPane gridPane = new GridPane();
		gridPane.getStyleClass().add("gridPane");
		gridPane.setVgap(12);
	    gridPane.setHgap(8);
	    
	    String[] tempArray = new String[30];
	    String[] catArray = new String[6];
		
	    String driverClass = "org.sqlite.JDBC";
		String url = "jdbc:sqlite:jeopardyDB.sqlite";
		
		//get categories
		try {
			Class.forName(driverClass);
			Connection connection = DriverManager.getConnection(url);
			
			String query = "SELECT Category FROM " + table + "";
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
	    } catch (SQLException | ClassNotFoundException e) {
	    	System.out.println(e);
	    }
		
		String categ = catArray[tab];
	    
	    int count = 0; 
	    
	    String query = "SELECT * FROM " + table + ""
	    		+ " WHERE Category = '" + categ + "'";
	    
		PreparedStatement stmt;
		
		try {
			Connection connection = DriverManager.getConnection(url);
			
			stmt = connection.prepareStatement(query);
			ResultSet results = stmt.executeQuery();  
			
			//add category
			Text cat_TXT = new Text("Category: ");
			cat_TXT.getStyleClass().add("qTXT");
		    TextField cat_TF = new TextField(categ);
		    cat_TF.getStyleClass().add("txtField");
		    GridPane.setHgrow(cat_TF, Priority.ALWAYS);
		    gridPane.add(cat_TXT , 0, count);
			gridPane.add(cat_TF, 1, count);
			
			int q = 0;
			
			//add questions and answers
			while(results.next()){
				String[] context = {results.getString("Question"),results.getString("Answer"),results.getString("Ans_a"),results.getString("Ans_b"),results.getString("Ans_c"),results.getString("Ans_d")};
			    String[] text = {"Question","Correct Answer:","Answer a:","Answer b:","Answer c:","Answer d:"};
				
				q++;
				count++;
				
				Text blank = new Text("");
			    gridPane.add(blank, 0, count); 
			    
			    count++;
			    
			    for(int i = 0; i < text.length; i++) {
			    	Text txt = new Text(text[i]);
			    	
			    	if(i == 0) {
			    		txt = new Text(text[0] + " " + q + ": ");
			    	}
			    	
			    	txt.getStyleClass().add("qTXT");
				    TextField txtField = new TextField(context[i]);
				    txtField.getStyleClass().add("txtField");
				    GridPane.setHgrow(txtField, Priority.ALWAYS);
				    gridPane.add(txt , 0, count);
					gridPane.add(txtField, 1, count); 
					
					count++;
			    }
	    	}
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return gridPane;
	}
	
	//update db to textfield inputs
	private class SaveButtonListener implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {				
			Set<Node> texts = root.lookupAll(".txtField");
			int nodeCnt = 0, qCnt = 0, catCnt = 0, id = 0;
			String category = "", question = "", answer = "", ansA = "", ansB = "", ansC = "", ansD = "";
			boolean newQ = false;
		
			for(Node temp : texts) {
				String info = ((TextField) temp).getText().replace('\'', ',');

				if(nodeCnt%31 == 0) { //category
					category = info;
					newQ = true;
					qCnt = 0;
					
					if(catCnt == 0)
						id = 25;
					else if(catCnt == 1) 
						id = 20;
					else if(catCnt == 2) 
						id = 15;
					else if(catCnt == 3) 
						id = 10;
					else if(catCnt == 4) 
						id = 5;
					else if(catCnt == 5) 
						id = 0;
					
					catCnt++;
				} else if(newQ == true) { //question
					question = info;
					newQ = false;
					qCnt++;
					id++;
				} else if(qCnt%6 == 1) { //answer
					answer = info;
					qCnt++;
				} else if(qCnt%6 == 2) { //option A
					ansA = info;
					qCnt++;
				} else if(qCnt%6 == 3) { //option B
					ansB = info;
					qCnt++;
				} else if(qCnt%6 == 4) { //option C
					ansC = info;
					qCnt++;
				} else if(qCnt%6 == 5) { //option D
					ansD = info;
					qCnt = 0;
					newQ = true;
					
					updateDB(category, question, answer, ansA, ansB, ansC, ansD, id);
				}				
				
				nodeCnt++;
		     }	
			
			System.out.println("\nupdate: entries saved to database");
		}
	}
	
	//connect to db
	private Connection connect() {
		String url = "jdbc:sqlite:jeopardyDB.sqlite";	
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return conn;
    }
	
	//update db to new entries
	public void updateDB(String c, String q, String a, String a_a, String a_b, String a_c, String a_d, int key) {		
		String sql = "UPDATE " + gameType.getValue() + " SET category = ? , "
                + "question = ? , "
                + "answer = ? , "
                + "Ans_a = ? , "
                + "Ans_b = ? , "
                + "Ans_c = ? , "
                + "Ans_d = ? "
                + "WHERE id = ?";

        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	
            pstmt.setString(1, c);
            pstmt.setString(2, q);
            pstmt.setString(3, a);
            pstmt.setString(4, a_a);
            pstmt.setString(5, a_b);
            pstmt.setString(6, a_c);
            pstmt.setString(7, a_d);
            pstmt.setInt(8, key);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
