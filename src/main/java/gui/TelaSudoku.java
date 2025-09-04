import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import logic.TabuleiroSudoku;

public class TelaSudoku extends Application {
        private TabuleiroSudoku tabuleiro;
    @Override
    public void start(Stage primaryStage) {
        // Título principal
        Label titulo = new Label("Sudoku");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        titulo.setTextFill(Color.web("#bfa14a"));

        // Subtítulo
        Label subtitulo = new Label("Feito por: Davi Castro");
        subtitulo.setFont(Font.font("Arial", FontWeight.NORMAL, 22));
        subtitulo.setTextFill(Color.web("#bfa14a"));

        // Botão Novo Jogo
        Button btnNovoJogo = new Button("Novo Jogo");
        btnNovoJogo.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        btnNovoJogo.setPrefWidth(300);
        btnNovoJogo.setPrefHeight(70);
        btnNovoJogo.setStyle(
            "-fx-background-color: #f7e7b6;" +
            "-fx-background-radius: 30;" +
            "-fx-text-fill: #bfa14a;" +
            "-fx-border-color: #bfa14a;" +
            "-fx-border-radius: 30;" +
            "-fx-border-width: 2;"
        );
        btnNovoJogo.setEffect(new DropShadow(10, Color.web("#e0c97f")));

        // Tema
        Label temaLabel = new Label("Tema:");
        temaLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        temaLabel.setTextFill(Color.web("#bfa14a"));

        Button temaBtn = new Button("Dia ☀️");
        temaBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        temaBtn.setPrefWidth(100);
        temaBtn.setStyle(
            "-fx-background-color: #f7e7b6;" +
            "-fx-background-radius: 20;" +
            "-fx-text-fill: #bfa14a;" +
            "-fx-border-color: #bfa14a;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 2;"
        );
        temaBtn.setEffect(new DropShadow(5, Color.web("#e0c97f")));

        HBox temaBox = new HBox(10, temaLabel, temaBtn);
        temaBox.setAlignment(Pos.CENTER);

        // Layout principal
        VBox layout = new VBox(30, titulo, subtitulo, btnNovoJogo, temaBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f8f1d6;");

        Scene scene = new Scene(layout, 1280, 720);

        primaryStage.setTitle("Sudoku - Tela Inicial");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}