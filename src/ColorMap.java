import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class ColorMap extends Application {

    private static void setColor(Pane p, Square s) {
        Image image;
        ImageView imageView;
        if (s.isObstacle()) {
            p.setStyle("-fx-background-color: #eb5d17");
            image = new Image("File:res/rock.png");
            imageView = new ImageView(image);
            imageView.fitHeightProperty().bind(p.heightProperty());
            imageView.fitWidthProperty().bind(p.widthProperty());
            p.getChildren().add(imageView);
        }
        else {
            switch (s.getDirType()) {
                case UP:
                    p.setStyle("-fx-background-color: #a4ff1d");
                    image = new Image("File:res/up-arrow.png");
                    imageView = new ImageView(image);
                    imageView.fitHeightProperty().bind(p.heightProperty());
                    imageView.fitWidthProperty().bind(p.widthProperty());
                    p.getChildren().add(imageView);
                    return;
                case DOWN:
                    p.setStyle("-fx-background-color: #35ee15");
                    image = new Image("File:res/down-arrow.png");
                    imageView = new ImageView(image);
                    imageView.fitHeightProperty().bind(p.heightProperty());
                    imageView.fitWidthProperty().bind(p.widthProperty());
                    p.getChildren().add(imageView);
                    return;
                case LEFT:
                    p.setStyle("-fx-background-color: lawngreen");
                    image = new Image("File:res/left-arrow.png");
                    imageView = new ImageView(image);
                    imageView.fitHeightProperty().bind(p.heightProperty());
                    imageView.fitWidthProperty().bind(p.widthProperty());
                    p.getChildren().add(imageView);
                    return;
                case RIGHT:
                    p.setStyle("-fx-background-color: #c5ff14");
                    image = new Image("File:res/right-arrow.png");
                    imageView = new ImageView(image);
                    imageView.fitHeightProperty().bind(p.heightProperty());
                    imageView.fitWidthProperty().bind(p.widthProperty());
                    p.getChildren().add(imageView);
                    return;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public static GridPane generateColorMap(Gridworld g) {

        Image image;
        ImageView imageView;

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
                if (i == mylocation_X && j == mylocation_Y) {
                    // creating pane
                    Pane pane = new Pane();
                    pane.setStyle("-fx-background-color: #ffffff");
                    pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    // inserting image
                    image = new Image("File:res/stickperson.png");
                    imageView = new ImageView(image);
                    imageView.setPreserveRatio(false);
                    imageView.fitHeightProperty().bind(pane.heightProperty());
                    imageView.fitWidthProperty().bind(pane.widthProperty());
                    pane.getChildren().add(imageView);
                    // adding pane to gridpane
                    gridPane.add(pane, j, i);
                }
                else if (i == goal_X && j == goal_Y) {
                    // creating pane
                    Pane pane = new Pane();
                    pane.setStyle("-fx-background-color: #7b3c0b");
                    pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    // inserting image
                    image = new Image("File:res/treasure.png");
                    imageView = new ImageView(image);
                    imageView.setPreserveRatio(false);
                    imageView.fitHeightProperty().bind(pane.heightProperty());
                    imageView.fitWidthProperty().bind(pane.widthProperty());
                    pane.getChildren().add(imageView);
                    // adding pane to gridpane
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

        int rowSize = 10;
        int colSize = 10;

        Gridworld g = new Gridworld(rowSize, colSize, true);

        g.applyArrows();
        GridPane gridPane = generateColorMap(g);
        primaryStage.setScene(new Scene(gridPane, 800, 800));
        primaryStage.setTitle("ColorMap - BEFORE");
        primaryStage.show();


        g.startGridworld();


        gridPane = generateColorMap(g);
        Stage secondStage = new Stage();
        secondStage.setScene(new Scene(gridPane, 800, 800));
        secondStage.setTitle("ColorMap - AFTER");
        secondStage.show();

    }
}
