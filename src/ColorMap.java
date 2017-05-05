
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class ColorMap extends Application {

    private static void setColor(Pane p, Square s) {
        if (s.isObstacle()) {
            p.setStyle("-fx-background-color: black");
        }
        else {
            switch (s.getDirType()) {
                case UP:
                    p.setStyle("-fx-background-color: green");
                    p.getChildren().add(new Label(s.getDirType().toString()));
                    return;
                case DOWN:
                    p.setStyle("-fx-background-color: darkgreen");
                    p.getChildren().add(new Label(s.getDirType().toString()));
                    return;
                case LEFT:
                    p.setStyle("-fx-background-color: lawngreen");
                    p.getChildren().add(new Label(s.getDirType().toString()));
                    return;
                case RIGHT:
                    p.setStyle("-fx-background-color: yellow");
                    p.getChildren().add(new Label(s.getDirType().toString()));
                    return;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public static GridPane generateColorMap(Gridworld g) {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);

        for (int i = 0; i < g.getHeightofBoard(); i++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            gridPane.getRowConstraints().add(rc);
        }
        for (int j = 0; j < g.getWidthofBoard(); j++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            gridPane.getColumnConstraints().add(cc);
        }

        int goal_X = g.getMygoal().getX();
        int goal_Y = g.getMygoal().getY();
        int mylocation_X = g.getMylocation().getX();
        int mylocation_Y = g.getMylocation().getY();

        for (int i = 0; i < g.getHeightofBoard(); i++) {
            for (int j = 0; j < g.getWidthofBoard(); j++) {
                if (i == mylocation_X && j == mylocation_Y){
                    Pane pane = new Pane();
                    pane.setStyle("-fx-background-color: blue");
                    pane.getChildren().add(new Label("Me"));
                    pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    gridPane.add(pane, j, i);
                }
                else if (i == goal_X && j == goal_Y) {
                    Pane pane = new Pane();
                    pane.setStyle("-fx-background-color: red");
                    pane.getChildren().add(new Label("Goal"));
                    pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    gridPane.add(pane, j, i);
                }
                else {
                    Pane pane = new Pane();
                    setColor(pane, g.getBoard()[i][j]);
                    pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    gridPane.add(pane, j, i);
                }
            }
        }
        return gridPane;
    }

    public void start(Stage primaryStage) {

        /** Specify whether to add randomly generated obstacles. False = no obstacles, True = obstacles */

        int rowSize = 25;
        int colSize = 25;

        Gridworld gWorld = new Gridworld(rowSize, colSize, true);

        gWorld.applyArrows();
        GridPane gridPane = generateColorMap(gWorld);
        primaryStage.setScene(new Scene(gridPane, 800, 800));
        primaryStage.setTitle("ColorMap - BEFORE");
        primaryStage.show();


        gWorld.printBoard();
        System.out.println();
        long x = 0;
        while (gWorld.getGammaDF() > .05) {
            gWorld.takeAction();
            x++;
        }
        gWorld.applyArrows();               // call this before printing board to set arrows!
        gWorld.printBoard();
        System.out.println("Episodes: " + x);


        gridPane = generateColorMap(gWorld);
        Stage secondStage = new Stage();
        secondStage.setScene(new Scene(gridPane, 800, 800));
        secondStage.setTitle("ColorMap - AFTER");
        secondStage.show();

    }
}
