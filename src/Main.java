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
import javafx.scene.paint.Paint;
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
			cells[row][col].circle = new Circle();
			{
				NumberBinding radiusProperty = Bindings
						.when(cells[row][col].boardSquare.widthProperty().greaterThan(cells[row][col].boardSquare.heightProperty()))
						.then(cells[row][col].boardSquare.heightProperty().subtract(12).divide(2))
						.otherwise(cells[row][col].boardSquare.widthProperty().subtract(12).divide(2));
				cells[row][col].circle.radiusProperty().bind(radiusProperty);
			}
		}
		cells[row][col].stackPane.setOnMouseEntered(e -> cells[row][col].boardSquare.highlight());
		cells[row][col].stackPane.setOnMouseExited(e -> cells[row][col].boardSquare.blacken());
		cells[row][col].stackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				
				if(isCorrectPositionToMoveTo(row, col))
					changeAllDiagonalAndPerpendicularCirclesColours(row, col);
			}
		});
		return cells[row][col].stackPane;
	}
	
	private void changeCircleColour(boolean isWhite, int row, int col)
	{
			if (isWhite)
				addCircleToTheBoard(row, col, Color.LAVENDERBLUSH);
			else
				addCircleToTheBoard(row, col, Color.BLACK);
	}
	
	private void getInitialPawnPosition()
	{
		changeCircleColour(true, 3, 3);
		changeCircleColour(false, 3, 4);
		changeCircleColour(false, 4, 3);
		changeCircleColour(true, 4, 4);
	}
	
	private boolean isCorrectPositionToMoveTo(int row, int col)
	{
		Paint colour = isFirstUser ? Color.LAVENDERBLUSH : Color.BLACK;
		return !isSameColourOrNull(colour, row-1, col)
				|| !isSameColourOrNull(colour, row-1, col-1)
				|| !isSameColourOrNull(colour, row, col-1)
				|| !isSameColourOrNull(colour, row+1, col+1)
				|| !isSameColourOrNull(colour, row+1, col)
				|| !isSameColourOrNull(colour, row, col+1);
	}
	
	private Paint getCircleColour(int row, int col)
	{
		return cells[row][col].circle.getFill();
	}
	
	private boolean isSameColourOrNull(Paint colour, int row, int col)
	{
		if (row >= ROW_COUNT || row < 0 || col >= COL_COUNT || col < 0)
			return false;
		return getCircleColour(row, col) == null || getCircleColour(row, col) == colour;
	}
	
	private void changeAllDiagonalAndPerpendicularCirclesColours(int row, int col)
	{
		Paint colour = isFirstUser ? Color.LAVENDERBLUSH : Color.BLACK;
		isFirstUser = !isFirstUser;
			
		for (int i = row; i < ROW_COUNT; i++)
		{
			if (!isSameColourOrNull(colour, i, col))
				addCircleToTheBoard(i, col, getOppositeColour(colour));
		}
		
		for (int i = col; i < COL_COUNT; i++)
		{
			if (!isSameColourOrNull(colour, row, i))
				addCircleToTheBoard(row, i, getOppositeColour(colour));
		}
		
		int r = 0;
		int c = 0;
		
		while (r < ROW_COUNT-1 && r > 0 && c < COL_COUNT-1 && c > 0)
		{
			if (!isSameColourOrNull(colour, r, c))
				addCircleToTheBoard(r, c, getOppositeColour(colour));
			r++;
			c++;
		}
		r = 0;
		c = 0;
		
		while (r < ROW_COUNT && r > 0 && c < COL_COUNT && c > 0)
		{
			if (!isSameColourOrNull(colour, r, c))
				addCircleToTheBoard(r, c, getOppositeColour(colour));
			r--;
			c--;
		}
	}
	
	private void addCircleToTheBoard(int row, int col, Paint colour)
	{
		cells[row][col].circle.setFill(colour);
		cells[row][col].stackPane.getChildren().add(cells[row][col].circle);
	}
	
	private Paint getOppositeColour(Paint colour)
	{
		if (colour == Color.LAVENDERBLUSH)
			return Color.BLACK;
		return Color.LAVENDERBLUSH;
	}
}
