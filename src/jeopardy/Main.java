/*
 * Jeopardy Game Desktop Application: launch
 */

package jeopardy;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	private Stage primaryStage;
	private BorderPane root = new BorderPane();
	private Login login = new Login();
	private final String styles = "GUI_styles.css";		

	@Override
	public void start(Stage primaryStage) {			
		try {
			this.primaryStage = primaryStage;
			this.primaryStage.setTitle("Jeopardy");

			this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	            @Override
	            public void handle(WindowEvent t) {
	            	Platform.exit();
	            }
	        });
			
			primaryStage.setMaximized(true);
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(styles);	
			
			root.setCenter(login.getLogin()); //show login window
						
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {	
		launch(args);
	}

}
