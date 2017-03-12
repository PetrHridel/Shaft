/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maturitniprace_v2;

/**
 *
 * @author Petr Hridel
 */
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private Stage primaryStage;
    private Canvas canvas;

    private final int WIDTH = 1200, HEIGHT = 800;

    private boolean gameOver, started;

    private Random rand = new Random();

    private Rect bird;
    private ArrayList<Rect> columns;

    private int yMotion;
    private int score = 0;
    private int ticks;

    private Logger logger = Logger.getGlobal();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        logger.log(Level.INFO, "Setting up the stage.");
        setupStage();
        logger.log(Level.INFO, "Stage setup.");
        logger.log(Level.INFO, "Setting up the scene.");
        setupScene();
        logger.log(Level.INFO, "Scene setup.");

        logger.log(Level.INFO, "Creating window.");
        canvas = new Canvas(WIDTH, HEIGHT);
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        logger.log(Level.INFO, "Window prepared.");
        logger.log(Level.INFO, "Opening window.");
        primaryStage.show();

        logger.log(Level.INFO, "Starting timer.");
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver) {
                    update();
                    repaint();
                }
            }
        }.start();
        logger.log(Level.INFO, "Timer started.");
    }

    private void setupStage() {
        primaryStage.setTitle("Flappy brick");

        // Setup events
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                jump();
            }
        });
        primaryStage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                jump();
            }
        });
    }

    private void setupScene() {
        bird = new Rect(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
        columns = new ArrayList<>();

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);
    }

    private void update() {
        logger.log(Level.INFO, "Update began.");
        int speed = 10;
        ticks++;

        if (started) {
            for (int i = 0; i < columns.size(); i++) {
                Rect column = columns.get(i);

                column.setX(column.getX() - speed);
            }

            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion += 2;
            }

            for (int i = 0; i < columns.size(); i++) {
                Rect column = columns.get(i);

                if (column.getX() + column.getWidth() < 0) {
                    columns.remove(column);

                    if (column.getY() == 0) {
                        addColumn(false);
                    }
                }
            }

            bird.setY(bird.getY() + yMotion);

            for (Rect column : columns) {
                if (column.getY() == 0 && bird.getX() + bird.getWidth() / 2 > column.getX() + column.getWidth() / 2 - 10 && bird.getX() + bird.getWidth() / 2 < column.getX() + column.getWidth() / 2 + 10) {
                    score++;
                }

                if (column.intersects(bird)) {
                    gameOver = true;

                    if (bird.getX() <= column.getX()) {
                        bird.setX(column.getX() - bird.getWidth());

                    } else {
                        if (column.getY() != 0) {
                            bird.setY(column.getY() - bird.getHeight());
                        } else if (bird.getY() < column.getHeight()) {
                            bird.setY(column.getHeight());
                        }
                    }
                }
            }

            if (bird.getY() > HEIGHT - 120 || bird.y < 0) {
                gameOver = true;
            }

            if (bird.getY() + yMotion >= HEIGHT - 120) {
                bird.setY(HEIGHT - 120 - bird.getHeight());

                gameOver = true;
            }
        }
        logger.log(Level.INFO, "Update ended.");
    }

    private void repaint() {
        logger.log(Level.INFO, "Repaint began.");
        GraphicsContext g = canvas.getGraphicsContext2D();
        //g.clearRect(0, 0, WIDTH, HEIGHT);

        g.setFill(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setFill(Color.ORANGE);
        g.fillRect(0, HEIGHT - 120, WIDTH, 120);

        g.setFill(Color.GREEN);
        g.fillRect(0, HEIGHT - 120, WIDTH, 20);

        g.setFill(Color.RED);
        g.fillRect(bird.getX(), bird.getY(), bird.getWidth(), bird.getHeight());

        for (Rect column : columns) {
            g.setFill(Color.DARKGREY);
            g.fillRect(column.getX(), column.getY(), column.getWidth(), column.getHeight());
        }

        g.setFill(Color.YELLOW);
        g.setFont(new Font("Arial", 100.0));

        if (!started) {
            g.fillText("Klikni a hraj!", 250, HEIGHT / 2 - 50);
        }

        if (gameOver) {
            g.fillText("Konec Hry!", 300, HEIGHT / 2 - 50);
        }

        if (!gameOver && started) {
            g.fillText(String.valueOf(score), WIDTH / 2 - 25, 100);
        }
        logger.log(Level.INFO, "Repaint ended.");
    }

    private void jump() {
        logger.log(Level.INFO, "Jump began.");
        if (gameOver) {
            bird = new Rect(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
            columns.clear();
            yMotion = 0;
            score = 0;

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;
        }

        if (!started) {
            started = true;
        } else if (!gameOver) {
            if (yMotion > 0) {
                yMotion = 0;
            }

            yMotion -= 10;
        }
        logger.log(Level.INFO, "Jump ended.");
    }

    private void addColumn(boolean start) {
        logger.log(Level.INFO, "Adding column began.");
        int space = 300;
        int width = 100;
        int height = 50 + rand.nextInt(300);

        if (start) {
            columns.add(new Rect(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
            columns.add(new Rect(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
        } else {
            columns.add(new Rect(columns.get(columns.size() - 1).getX() + 600, HEIGHT - height - 120, width, height));
            columns.add(new Rect(columns.get(columns.size() - 1).getX(), 0, width, HEIGHT - height - space));
        }
        logger.log(Level.INFO, "Adding column ended.");
    }

    public static void main(String[] args) {
        launch(args);
    }

    private class Rect {

        private int x = 0;
        private int y = 0;
        private int width = 0;
        private int height = 0;

        public Rect() {
            this(0, 0, 0, 0);
        }

        public Rect(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean intersects(Rect rect) {
            return (rect.x >= this.x && rect.x <= this.x + this.width)
                    && (rect.y >= this.y && rect.y <= this.y + this.height);
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
