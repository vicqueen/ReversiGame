import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

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
				board.add(getBoardSquare(row, col), col, row);
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
				
				cells[row][col].circle.setFill(Color.TRANSPARENT);
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
		return !isCirclePresent(row, col) 
				&& (!isSameColourOrNull(colour, row-1, col-1)
				|| !isSameColourOrNull(colour, row-1, col)
				|| !isSameColourOrNull(colour, row-1, col+1)
				|| !isSameColourOrNull(colour, row, col-1)
				|| !isSameColourOrNull(colour, row, col+1)
				|| !isSameColourOrNull(colour, row+1, col-1)
				|| !isSameColourOrNull(colour, row+1, col)
				|| !isSameColourOrNull(colour, row+1, col+1));
	}
	
	private Paint getCircleColour(int row, int col)
	{
		return cells[row][col].circle.getFill();
	}
	
	private boolean isSameColourOrNull(Paint colour, int row, int col)
	{
		if (row >= ROW_COUNT || row < 0 || col >= COL_COUNT || col < 0)
			return false;
		return getCircleColour(row, col).equals(Color.TRANSPARENT) || getCircleColour(row, col).equals(colour);
	}
	
	private void changeAllDiagonalAndPerpendicularCirclesColours(int row, int col)
	{
		Paint colour = isFirstUser ? Color.LAVENDERBLUSH : Color.BLACK;
		isFirstUser = !isFirstUser;
		
		changeAllCirclesColourVertically(true, colour, row, col);
		changeAllCirclesColourVertically(false, colour, row, col);
		
		changeAllCirclesColourHorizontally(true, colour, row, col);
		changeAllCirclesColourHorizontally(false, colour, row, col);
		
		changeAllCirclesColoursDiagonally(colour, row, col);
	}
	
	private void changeAllCirclesColoursDiagonally(Paint colour, int row, int col)
	{
		changeAllCirclesColoursInOneDiagonalDirection(true, true, colour, row, col);
		changeAllCirclesColoursInOneDiagonalDirection(true, false, colour, row, col);
		changeAllCirclesColoursInOneDiagonalDirection(false, true, colour, row, col);
		changeAllCirclesColoursInOneDiagonalDirection(false, false, colour, row, col);
	}
	
	private void changeAllCirclesColoursInOneDiagonalDirection(boolean shouldRowIncrease, boolean shouldColIncrease, Paint colour, int row, int col)
	{
		if (isSameColourCircleAtTheEndDiagonally(shouldRowIncrease, shouldColIncrease, colour, row, col))
		{
			int r = increaseOrDecreaseValue(shouldRowIncrease, row);;
			int c = increaseOrDecreaseValue(shouldColIncrease, col);;
			
			while (!isSameColourOrNull(colour, r, c))
			{
				addCircleToTheBoard(row, col, colour);
				addCircleToTheBoard(r, c, colour);
				r = increaseOrDecreaseValue(shouldRowIncrease, r);
				c = increaseOrDecreaseValue(shouldColIncrease, c);
			}
		}
	}
	
	private boolean isSameColourCircleAtTheEndDiagonally(boolean shouldRowIncrease, boolean shouldColIncrease, Paint colour, int row, int col)
	{
		int r = increaseOrDecreaseValue(shouldRowIncrease, row);;
		int c = increaseOrDecreaseValue(shouldColIncrease, col);;
		
		while (!isSameColourOrNull(colour, r, c))
		{
			r = increaseOrDecreaseValue(shouldRowIncrease, r);
			c = increaseOrDecreaseValue(shouldColIncrease, c);
		}
		
		return getCircleColour(r, c).equals(colour);
	}
	
	private int increaseOrDecreaseValue(boolean shouldIncrease, int number)
	{
		return shouldIncrease ? ++number : --number;
	}
	
	private void changeAllCirclesColourVertically(boolean shouldGoUp, Paint colour, int row, int col) {
		
		if (isSameColourCircleAtTheEndVertically(shouldGoUp, colour, row, col))
		{
			int i = increaseOrDecreaseValue(shouldGoUp, row);
			
			while (!isSameColourOrNull(colour, i, col))
			{
				addCircleToTheBoard(row, col, colour);
				addCircleToTheBoard(i, col, colour);
				i = increaseOrDecreaseValue(shouldGoUp, i);
			}
		}
	}
	
	private boolean isSameColourCircleAtTheEndVertically(boolean shouldGoUp, Paint colour, int row, int col)
	{
		int i = increaseOrDecreaseValue(shouldGoUp, row);
		
		while (!isSameColourOrNull(colour, i, col))
			i = increaseOrDecreaseValue(shouldGoUp, i);
		
		return getCircleColour(i, col).equals(colour);
	}
	
	private void changeAllCirclesColourHorizontally(boolean shouldGoRight, Paint colour, int row, int col)
	{
		if (isSameColourCircleAtTheEndHorizontally(shouldGoRight, colour, row, col))
		{
			int i = increaseOrDecreaseValue(shouldGoRight, col);
			while (!isSameColourOrNull(colour, row, i))
			{
				addCircleToTheBoard(row, col, colour);
				addCircleToTheBoard(row, i, colour);
				i = increaseOrDecreaseValue(shouldGoRight, i);
			}
		}
	}
	
	private boolean isSameColourCircleAtTheEndHorizontally(boolean shouldGoRight, Paint colour, int row, int col)
	{
		int i = increaseOrDecreaseValue(shouldGoRight, col);
		
		while (!isSameColourOrNull(colour, row, i))
			i = increaseOrDecreaseValue(shouldGoRight, i);
		
		return getCircleColour(row, i).equals(colour);
	}
	
	private void addCircleToTheBoard(int row, int col, Paint colour)
	{
		cells[row][col].circle.setFill(colour);
		if (!isCirclePresent(row, col))
			cells[row][col].stackPane.getChildren().add(cells[row][col].circle);
	}
	
	private boolean isCirclePresent(int row, int col)
	{
		return cells[row][col].stackPane.getChildren().contains(cells[row][col].circle);
	}
}
