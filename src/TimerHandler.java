import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;

public class TimerHandler implements EventHandler<ActionEvent> {

  private Label label;
  private int seconds = 0;
  private int minutes = 0;
  private int hours = 0;

  public TimerHandler(Label label) {
    this.label = label;
  }

  @Override
  public void handle(ActionEvent arg0) {
    seconds++;
    if (seconds == 60) {
      seconds = 0;
      minutes++;
    }
    if (minutes == 60) {
      minutes = 0;
      hours++;
    }
    label.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
  }
};