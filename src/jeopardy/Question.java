/*
 * Jeopardy Game Desktop Application: question/answer popup (opens on top of gameboard)
 */

package jeopardy;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Question {
	private final BorderPane root;
	final String styles = "GUI_styles.css";	
	
	private Button[] ansBtns = new Button[4];
	private Button close = new Button("close");
	
	private String answer = "";
	private int points = GameBoard.getPoints();
	private static int score = 0;
	
	//build ui
	public Question() {
		root = new BorderPane();
		root.setPadding(new Insets(0, 0, 0, 0));
		root.setId("question");
		
		String quest = GameBoard.getQuestion();
		answer = GameBoard.getAnswer();
		
		TextArea question = new TextArea("");
		question.setText(quest);
		question.setWrapText(true);
		question.setEditable(false);
		
		for(int i = 0; i < ansBtns.length; i++) {
			ansBtns[i] = new Button("answer"+(i+1));
			ansBtns[i].getStyleClass().add("answerBtns");
			
			switch(i) {
				case 0:
					ansBtns[i].setText(GameBoard.getANS_a());
					break;
				case 1:
					ansBtns[i].setText(GameBoard.getANS_b());
					break;
				case 2:
					ansBtns[i].setText(GameBoard.getANS_c());
					break;
				case 3:
					ansBtns[i].setText(GameBoard.getANS_d());
					break;
				default: break;
			}
			
			ansBtns[i].setOnAction(new AnswerButtonListener());
			ansBtns[i].setId("ans_"+i);
		}
		
		HBox closeBox = new HBox();
		closeBox.getChildren().addAll(close);
		closeBox.setAlignment(Pos.TOP_RIGHT);
		close.setOnAction(new CloseButtonListener());
		close.setId("closeBtn");
		close.setDisable(true);

		VBox questBox = new VBox();
		questBox.getChildren().add(question);
		questBox.setAlignment(Pos.CENTER);
		questBox.setId("questBox");
		
		VBox ansBox = new VBox();
		ansBox.getChildren().addAll(ansBtns);
		ansBox.setSpacing(8);
		ansBox.setId("ansBox");
		ansBox.setAlignment(Pos.CENTER);
		
		root.setTop(closeBox);
		root.setCenter(questBox);
	    root.setBottom(ansBox);		
	}
	
	//return ui
	public BorderPane getQuestion() {
        return root;
    }
	
	//if user is correct display in green, otherwise display in red and highlight correct answer
	private class AnswerButtonListener implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {	
			close.setDisable(false);
			
			Object src = event.getSource();
			((Node) src).setDisable(true);
			String[] source = ((Node) src).getId().split("_");
			int num = Integer.parseInt(source[1]);
			
			//if correct
			if(ansBtns[num].getText().equals(answer)) {
				ansBtns[num].setStyle("-fx-background-color: GREEN;" + 
						" 	-fx-text-fill: YELLOW;" + 
						" 	-fx-border-color: BLACK;");
				score = points;
				ansBtns[num].setDisable(false);
				ansBtns[num].setMouseTransparent(true);
			//if wrong
			} else {
				ansBtns[num].setStyle("-fx-background-color: RED;" + 
						" 	-fx-text-fill: YELLOW;" + 
						" 	-fx-border-color: BLACK;");
				score = 0 - points; 
				
				ansBtns[num].setDisable(false);
				ansBtns[num].setMouseTransparent(true);
				
				//highlight correct
				for(int i = 0; i < ansBtns.length; i++) {
					if(ansBtns[i].getText().equals(answer)) {
						ansBtns[i].setStyle("-fx-background-color: GREEN;" + 
								" 	-fx-text-fill: YELLOW;" + 
								" 	-fx-border-color: BLACK;");
					}
				}
			}
			
			//disable all other btns
			for(int i = 0; i < ansBtns.length; i++) {
				if(i != num) {
					ansBtns[i].setDisable(true);
				}
			} 	
			
			updateScore(score);
		}
	}
	
	//close q/a popup and reset q/a buttons
	private class CloseButtonListener implements EventHandler<ActionEvent> {
		public void handle(ActionEvent event) {
			Stage stage = (Stage) close.getScene().getWindow();
		    stage.close();
		    close.setDisable(true);
		    
		    for(int i = 0; i < ansBtns.length; i++) {
				ansBtns[i].setDisable(false);
				ansBtns[i].setStyle(null);
			}
		}
	}
	
	public void updateScore(int s) {
		Player.setScore(s);
		resetScore();
	}
	
	public static int getScore() {
		return score;
	}
	
	public static void resetScore() {
		score = 0;
	}
}
