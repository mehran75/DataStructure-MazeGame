package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.EmptyStackException;
import java.util.EventListener;
import java.util.Stack;
import java.util.Timer;

public class Main extends Application {

    class Index {
        public int i;
        public int j;

        public Index(int i, int j) {
            this.i = i;
            this.j = j;
        }


        @Override
        public boolean equals(Object obj) {
            Index index = (Index) obj;
            if (this.i == index.i && this.j == index.j)
                return true;
            return false;
        }
    }

    private boolean[][] matrix = {
            {true, false, false, false, false, false, true, true, true, true,},
            {true, true, true, true, true, false, true, false, true, true},
            {true, true, true, false, true, false, true, true, true, true},
            {true, false, true, false, true, false, true, false, false, true},
            {true, false, true, false, true, false, true, false, true, true},
            {true, false, false, false, true, false, true, false, true, false},
            {true, false, true, true, true, false, true, false, true, false,},
            {true, false, true, false, false, false, true, false, true, true},
            {false, true, true, true, true, true, true, false, true, true},
            {true, true, true, true, false, false, false, false, true, true}
    };


    private Button button;
    private TextField size_tf, startPoint_tf, endPoint_tf;
    private int num;
    private GridPane gridPane;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Maze");

        button = new Button("change");
        size_tf = new TextField("10");
        startPoint_tf = new TextField("0-0");
        endPoint_tf = new TextField("9-9");

        size_tf.setPromptText("size");

        startPoint_tf.setPromptText("start");
        startPoint_tf.setMaxWidth(60);

        endPoint_tf.setPromptText("end");
        endPoint_tf.setMaxWidth(60);

        HBox hBox = new HBox(new Label("start point :"), startPoint_tf, new Label("end Point :"), endPoint_tf
                , new Label("number :"), size_tf, button);

        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(3);

        GridPane gameLayout = drawTable(matrix, 10, 650, 650);


        BorderPane root = new BorderPane(gameLayout);
        root.setTop(hBox);

        primaryStage.setScene(new Scene(root, 680, 700, Color.WHITE));
        primaryStage.show();

        button.setOnAction(e -> {

            Stage stage = new Stage();

            GridPane matrixPane = new GridPane();
            matrixPane.setAlignment(Pos.CENTER);
            matrixPane.setGridLinesVisible(true);
            matrixPane.setMaxSize(400, 400);


            num = Integer.parseInt(size_tf.getText());

            ToggleButton[][] toggleButtons = new ToggleButton[num][num];

            for (int i = 0; i < num; i++)
                for (int j = 0; j < num; j++) {
                    ToggleButton tButton = new ToggleButton("1");
                    tButton.setPrefWidth(400 / num);
                    tButton.setPrefHeight(400 / num);
                    tButton.setSelected(true);
                    toggleButtons[i][j] = tButton;

                    tButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable,
                                            Boolean oldValue, Boolean newValue) {
                            if (!newValue) {
                                tButton.setText("0");
                            } else {
                                tButton.setText("1");
                            }
                        }
                    });
                }


            for (int i = 0; i < num; i++)
                for (int j = 0; j < num; j++) {
                    matrixPane.add(toggleButtons[j][i], j, i);
                }

            boolean[][] matrix = new boolean[num][num];


            Button button = new Button("create!");
            button.setOnAction(event -> {
                for (int i = 0; i < num; i++)
                    for (int j = 0; j < num; j++)
                        if (toggleButtons[j][i].isSelected())
                            matrix[i][j] = true;
                        else
                            matrix[i][j] = false;

                gridPane = drawTable(matrix, num, 650, 650);
                root.setCenter(gridPane);
                primaryStage.show();
                stage.close();
            });

            FlowPane flowPane = new FlowPane(matrixPane, button);
            flowPane.setAlignment(Pos.CENTER);

            ScrollPane scrollPane = new ScrollPane(flowPane);

            Scene scene = new Scene(scrollPane);
            stage.setScene(scene);
            stage.show();
        });
    }


    private GridPane drawTable(boolean[][] matrix, int num, int screenWidth, int screenHeight) {
        this.num = num;

        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setGridLinesVisible(true);


        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {


                Rectangle rectangle = new Rectangle((screenWidth) / num,
                        (screenHeight) / num);


                if (matrix[i][j])
                    rectangle.setFill(Color.TRANSPARENT);
                else
                    rectangle.setFill(Color.THISTLE);

                root.add(rectangle, j, i);

            }
        }

        gridPane = root;

        int sX, sY, eX, eY;

        sX = Integer.parseInt(startPoint_tf.getText().split("-")[1]);
        sY = Integer.parseInt(startPoint_tf.getText().split("-")[0]);
        eX = Integer.parseInt(endPoint_tf.getText().split("-")[1]);
        eY = Integer.parseInt(endPoint_tf.getText().split("-")[0]);

        DFS(matrix, sX, sY, eX, eY, screenWidth, screenHeight);

        return root;

    }


    public static void main(String[] args) {
        launch(args);


    }


    void DFS(boolean[][] m, int startPointX, int startPointY, int endPointX, int endPoinY, int screenW, int screenH) {

        Stack<Index> stack = new Stack<>();

        stack.push(new Index(startPointX, startPointY));


        while (!stack.isEmpty()) {

            Index index = stack.peek();
            int i = index.i,
                    j = index.j;

            Index[] sidesindexes = {
                    new Index(i - 1, j),
                    new Index(i, j - 1),
                    new Index(i + 1, j),
                    new Index(i, j + 1)
            };


            if (i == endPointX && j == endPoinY)
                break;

            if (j != num - 1)
                if (m[i][j + 1] && !stack.contains(sidesindexes[3])) {
                    stack.push(new Index(i, j + 1));
                    continue;
                }

            if (i != num - 1)
                if (m[i + 1][j] && !stack.contains(sidesindexes[2])) {
                    stack.push(new Index(i + 1, j));
                    continue;
                }


            if (j != 0)
                if (m[i][j - 1] && !stack.contains(sidesindexes[1])) {
                    stack.push(new Index(i, j - 1));
                    continue;
                }

            if (i != 0)
                if (m[i - 1][j] && !stack.contains(sidesindexes[0])) {
                    stack.push(new Index(i - 1, j));
                    continue;
                }


            Index useless = stack.pop();
            m[useless.i][useless.j] = false;


        }

        if (stack.isEmpty()) {
            System.out.println("no way");
            return;
        }


        Stack<Index> reverse = new Stack<>();

        while (!stack.isEmpty()) {
            reverse.push(stack.pop());
        }


        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(.09f), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                drawRoute(reverse, screenW, screenH);

            }
        }));
        timeline.setCycleCount(reverse.size());
        timeline.play();


    }

    void drawRoute(Stack<Index> reverse, int screenW, int screenH) {

        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(0.0, 0.0,
                (double) ((screenW) / num), 0.0,
                (double) (((screenW) / num) / 2), (double) ((screenW) / num));
        triangle.setFill(Color.GOLD);
        triangle.setScaleX(.3);
        triangle.setScaleY(.3);

        Index index = reverse.pop();


        int i = index.i, j = index.j;
        try {

            Index next = reverse.peek();

            i = next.i;
            j = next.j;

        } catch (EmptyStackException e) {

        }


        if (i != index.i || j != index.j) {

            if (i < index.i) {
                triangle.setRotate(180);
            }

            if (j < index.j) {
                triangle.setRotate(90);
            } else if (j > index.j) {
                triangle.setRotate(-90);
            }
        }

        if (index.i == Integer.parseInt(startPoint_tf.getText().split("-")[0]) &&
                index.j == Integer.parseInt(startPoint_tf.getText().split("-")[1]) ){
            Circle circle = new Circle((screenW / num) / 2);
            circle.setFill(Color.RED);
            circle.setScaleX(.3);
            circle.setScaleY(.3);
            gridPane.add(circle, index.j, index.i);
        }
        else if (reverse.size() >= 1)
            gridPane.add(triangle, index.j, index.i);
        else {
            Circle circle = new Circle((screenW / num) / 2);
            circle.setFill(Color.GREEN);
            circle.setScaleX(.3);
            circle.setScaleY(.3);
            gridPane.add(circle, index.j, index.i);
        }


    }
}
