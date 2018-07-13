package cmdp;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		try {
			URL url = new File("src/cmdp/MDPGen.fxml").toURL();
			Parent root = FXMLLoader.load(url);

			 primaryStage.setTitle("MDP模型生成器软件");
	         primaryStage.setScene(new Scene(root));
	         primaryStage.show();
	         
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	 public static void main(String[] args) {
	        launch(args);
	 }
}
