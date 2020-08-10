/*
 * Jeopardy Game Desktop Application: player ui (with name and points)
 */

package jeopardy;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class Player {	
	private static int score;
	private static Label scoreBoard = new Label("$0");
	private Label name = new Label();
	private String user = "";
	private final BorderPane root;
	
	//build ui
	public Player() {
		root = new BorderPane();
		root.setPadding(new Insets(0, 0, 0, 0));	    
		
		name.setWrapText(true);
		name.setMaxWidth(100);
		name.setAlignment(Pos.CENTER);
		name.setId("name");
		
		scoreBoard.setText("$" + score);	
		scoreBoard.setId("scoreBoard");
		
		VBox playerTxt = new VBox(new Text(""), new Text(""), new Text(""), scoreBoard, name);
		playerTxt.setSpacing(25);
		playerTxt.setAlignment(Pos.TOP_CENTER);
		
		ImageView imgV = new ImageView(new Image(getClass().getResourceAsStream("player.png")));
	    StackPane pane = new StackPane();

	    pane.getChildren().add(imgV);
	    pane.getChildren().add(playerTxt);
	    pane.setAlignment(Pos.BOTTOM_CENTER);

		root.setCenter(pane);
	}
	
	//return ui
	public BorderPane getPlayers() {
        return root;
    }
	
	//called when question is answered to update scoreboard
	public static void setScore(int s) {
		score += Question.getScore();
		scoreBoard.setText("$" + score);	
		Question.resetScore();
		
		System.out.println(score);
	}

	public void getPlayerName(String n) {
		user = n;
		name.setText(user);
	}
}
