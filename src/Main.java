import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.When;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.stage.FileChooser;

public class Main extends Application {

	private final int ROW_COUNT = 8;
	private final int COL_COUNT = 8;
	private boolean isFirstUser = true;
	private Cell[][] cells = new Cell[ROW_COUNT][COL_COUNT];

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Scene scene = new Scene(getBoard(), 600, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public GridPane getBoard() {
		GridPane board = new GridPane();
		for (int row = 0; row < ROW_COUNT; row++) {
			for (int col = 0; col < COL_COUNT; col++) {
				board.add(getBoardSquare(row, col), row, col);
			}
		}
		for (int row = 0; row < ROW_COUNT; row++) {
			RowConstraints constrains = new RowConstraints();
			constrains.setPercentHeight(20);
			;
			board.getRowConstraints().add(constrains);
		}
		for (int col = 0; col < COL_COUNT; col++) {
			ColumnConstraints constrains = new ColumnConstraints();
			constrains.setPercentWidth(20);
			board.getColumnConstraints().add(constrains);
		}
		getInitialPawnPosition();
		
		return board;
	}

	public StackPane getBoardSquare(int row, int col) {
		cells[row][col] = new Cell(); {
			cells[row][col].boardSquare = new BoardSquare(Color.HOTPINK);
			cells[row][col].stackPane = new StackPane(cells[row][col].boardSquare);
		}
		cells[row][col].stackPane.setOnMouseEntered(e -> cells[row][col].boardSquare.highlight());
		cells[row][col].stackPane.setOnMouseExited(e -> cells[row][col].boardSquare.blacken());
		cells[row][col].stackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				cells[row][col].circle = new Circle();
				{
					NumberBinding radiusProperty = Bindings
							.when(cells[row][col].boardSquare.widthProperty().greaterThan(cells[row][col].boardSquare.heightProperty()))
							.then(cells[row][col].boardSquare.heightProperty().subtract(12).divide(2))
							.otherwise(cells[row][col].boardSquare.widthProperty().subtract(12).divide(2));
					cells[row][col].circle.radiusProperty().bind(radiusProperty);
					if (isFirstUser)
					{
						cells[row][col].circle.setFill(Color.LAVENDERBLUSH);
						isFirstUser = false;
					}
					else
					{
						cells[row][col].circle.setFill(Color.BLACK);
						isFirstUser = true;
					}
				}
				
				if (cells[row][col].stackPane.getChildren().contains(cells[row][col].circle)) {
					cells[row][col].stackPane.getChildren().remove(cells[row][col].circle);
				} else {
					cells[row][col].stackPane.getChildren().add(cells[row][col].circle);
				}
			}
		});
		return cells[row][col].stackPane;
	}
	
	private Circle getColouredCircle(boolean isWhite, BoardSquare square)
	{
		Circle circle = new Circle();
		{
			NumberBinding radiusProperty = Bindings
					.when(square.widthProperty().greaterThan(square.heightProperty()))
					.then(square.heightProperty().subtract(12).divide(2))
					.otherwise(square.widthProperty().subtract(12).divide(2));
			circle.radiusProperty().bind(radiusProperty);
			if (isWhite)
				circle.setFill(Color.LAVENDERBLUSH);
			else
				circle.setFill(Color.BLACK);

		}
		return circle;
	}
	
	private void getInitialPawnPosition()
	{
		cells[3][3].stackPane.getChildren().add(getColouredCircle(true, cells[3][3].boardSquare));
		cells[3][4].stackPane.getChildren().add(getColouredCircle(false, cells[3][3].boardSquare));
		cells[4][3].stackPane.getChildren().add(getColouredCircle(false, cells[3][3].boardSquare));
		cells[4][4].stackPane.getChildren().add(getColouredCircle(true, cells[3][3].boardSquare));
	}
}
