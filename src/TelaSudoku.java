import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TelaSudoku extends Application {

    private boolean temaEscuro = false; // controla o tema
    private Stage mainStage; // para poder voltar para a tela inicial

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        mostrarTelaInicial();
    }

    private void mostrarTelaInicial() {
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

        Button temaBtn = new Button(temaEscuro ? "Noite 🌙" : "Dia ☀");
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

        // Dificuldade (criada mas não adicionada ainda)
        Label dificuldadeLabel = new Label("Escolha a dificuldade:");
        dificuldadeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        dificuldadeLabel.setTextFill(Color.web("#bfa14a"));

        Button facilBtn = new Button("Fácil");
        Button medioBtn = new Button("Médio");
        Button dificilBtn = new Button("Difícil");

        for (Button btn : new Button[]{facilBtn, medioBtn, dificilBtn}) {
            btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            btn.setStyle(
                    "-fx-background-color: #f7e7b6;" +
                            "-fx-background-radius: 20;" +
                            "-fx-text-fill: #bfa14a;" +
                            "-fx-border-color: #bfa14a;" +
                            "-fx-border-radius: 20;" +
                            "-fx-border-width: 2;"
            );
            btn.setEffect(new DropShadow(5, Color.web("#e0c97f")));
            btn.setPrefWidth(90);
        }

        HBox dificuldadeBox = new HBox(20, facilBtn, medioBtn, dificilBtn);
        dificuldadeBox.setAlignment(Pos.CENTER);

        VBox dificuldadeContainer = new VBox(15, dificuldadeLabel, dificuldadeBox);
        dificuldadeContainer.setAlignment(Pos.CENTER);
        dificuldadeContainer.setOpacity(0); // começa invisível

        // Layout principal: temaBox já está logo abaixo do btnNovoJogo
        VBox layout = new VBox(30, titulo, subtitulo, btnNovoJogo, temaBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f8f1d6;");

        // Alterna a exibição das dificuldades ao clicar em "Novo Jogo"
        btnNovoJogo.setOnAction(e -> {
            int temaIndex = layout.getChildren().indexOf(temaBox);
            if (!layout.getChildren().contains(dificuldadeContainer)) {
                // Adiciona e anima aparecendo
                layout.getChildren().add(temaIndex, dificuldadeContainer);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(400), dificuldadeContainer);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            } else {
                // Anima sumindo e remove ao final
                FadeTransition fadeOut = new FadeTransition(Duration.millis(400), dificuldadeContainer);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(ev -> layout.getChildren().remove(dificuldadeContainer));
                fadeOut.play();
            }
        });

        // --- Tema claro/escuro ---
        temaBtn.setOnAction(e -> {
            temaEscuro = !temaEscuro;
            if (temaEscuro) {
                temaBtn.setText("Noite 🌙");
                layout.setStyle("-fx-background-color: #232323;");
                titulo.setTextFill(Color.web("#f7e7b6"));
                subtitulo.setTextFill(Color.web("#f7e7b6"));
                temaLabel.setTextFill(Color.web("#f7e7b6"));
            } else {
                temaBtn.setText("Dia ☀");
                layout.setStyle("-fx-background-color: #f8f1d6;");
                titulo.setTextFill(Color.web("#bfa14a"));
                subtitulo.setTextFill(Color.web("#bfa14a"));
                temaLabel.setTextFill(Color.web("#bfa14a"));
            }
        });

        // --- Ação ao clicar em uma dificuldade: vai para o tabuleiro ---
        facilBtn.setOnAction(e -> mostrarTabuleiro(mainStage, "fácil"));
        medioBtn.setOnAction(e -> mostrarTabuleiro(mainStage, "médio"));
        dificilBtn.setOnAction(e -> mostrarTabuleiro(mainStage, "difícil"));

        Scene scene = new Scene(layout, 1280, 720);

        mainStage.setTitle("Sudoku");
        mainStage.setScene(scene);
        mainStage.show();
    }

    // Método para mostrar a tela do tabuleiro e permitir jogar
    private void mostrarTabuleiro(Stage stage, String dificuldade) {
        TabuleiroSudoku tabuleiro = new TabuleiroSudoku();
        tabuleiro.gerarTabuleiroComDificuldade(dificuldade);

        // Timer
        Label timerLabel = new Label("Tempo: 00:00");
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        timerLabel.setTextFill(temaEscuro ? Color.web("#f7e7b6") : Color.web("#bfa14a"));

        final int[] seconds = {0};
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            seconds[0]++;
            int min = seconds[0] / 60;
            int sec = seconds[0] % 60;
            timerLabel.setText(String.format("Tempo: %02d:%02d", min, sec));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Botão Voltar
        Button btnVoltar = new Button("Voltar");
        btnVoltar.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        btnVoltar.setStyle(
                "-fx-background-color: #f7e7b6;" +
                        "-fx-background-radius: 20;" +
                        "-fx-text-fill: #bfa14a;" +
                        "-fx-border-color: #bfa14a;" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 2;"
        );
        btnVoltar.setOnAction(e -> {
            timeline.stop();
            mostrarTelaInicial();
        });

        HBox topo = new HBox(20, btnVoltar, timerLabel);
        topo.setAlignment(Pos.CENTER_LEFT);
        topo.setPrefWidth(1280);
        topo.setSpacing(40);
        topo.setStyle("-fx-padding: 20 0 0 40;");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(0);
        grid.setVgap(0);

        String corFundo = temaEscuro ? "#232323" : "#f8f1d6";
        String corCelula = temaEscuro ? "#353535" : "#fffbe6";
        String corCelulaFixa = temaEscuro ? "#444466" : "#e6e6ff";
        String corTexto = temaEscuro ? "#f7e7b6" : "#bfa14a";
        String corTextoFixa = temaEscuro ? "#fff" : "#232323";
        String corBordaBloco = temaEscuro ? "#fff" : "#000";

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Integer valor = tabuleiro.getValor(i, j);
                Button cell = new Button(valor == null ? "" : valor.toString());
                cell.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 30));
                cell.setPrefSize(60, 60);

                // Calcula as larguras das bordas
                int top = (i == 0) ? 4 : (i % 3 == 0 ? 3 : 1);
                int left = (j == 0) ? 4 : (j % 3 == 0 ? 3 : 1);
                int right = (j == 8) ? 4 : 1;
                int bottom = (i == 8) ? 4 : 1;

                String border = String.format(
                        "-fx-border-color: %s; " +
                                "-fx-border-width: %d %d %d %d; " +
                                "-fx-border-style: solid;",
                        corBordaBloco, top, right, bottom, left
                );

                if (tabuleiro.isFixa(i, j)) {
                    cell.setStyle(
                            "-fx-background-color: " + corCelulaFixa + ";" +
                                    border +
                                    "-fx-text-fill: " + corTextoFixa + ";" +
                                    "-fx-font-weight: bold;"
                    );
                    cell.setDisable(true);
                } else {
                    cell.setStyle(
                            "-fx-background-color: " + corCelula + ";" +
                                    border +
                                    "-fx-text-fill: " + corTexto + ";" +
                                    "-fx-font-weight: bold;"
                    );
                    final int linha = i;
                    final int coluna = j;
                    cell.setOnAction(e -> {
                        TextInputDialog dialog = new TextInputDialog();
                        dialog.setTitle("Preencher célula");
                        dialog.setHeaderText("Digite um número de 1 a 9 (ou deixe vazio para apagar):");
                        dialog.setContentText("Valor:");

                        dialog.showAndWait().ifPresent(input -> {
                            if (input.isEmpty()) {
                                tabuleiro.setarValorTabuleiro(linha, coluna, null);
                                cell.setText("");
                            } else {
                                try {
                                    int num = Integer.parseInt(input);
                                    if (num >= 1 && num <= 9 && tabuleiro.validarJogada(linha, coluna, num)) {
                                        tabuleiro.setarValorTabuleiro(linha, coluna, num);
                                        cell.setText(String.valueOf(num));
                                        // Checa vitória
                                        if (tabuleiro.validarTabuleiroParalelo()) {
                                            timeline.stop();
                                            Alert win = new Alert(Alert.AlertType.INFORMATION);
                                            win.setTitle("Parabéns!");
                                            win.setHeaderText(null);
                                            win.setContentText("Você completou o Sudoku em " + timerLabel.getText().replace("Tempo: ", "") + "!");
                                            win.showAndWait();
                                            mostrarTelaInicial(); // Volta para tela inicial após OK
                                        }
                                    } else {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Valor inválido");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Número inválido ou jogada não permitida!");
                                        alert.showAndWait();
                                    }
                                } catch (NumberFormatException ex) {
                                    // Ignora entradas não numéricas
                                }
                            }
                        });
                    });
                }
                grid.add(cell, j, i);
            }
        }

        VBox root = new VBox(20, topo, grid);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: " + corFundo + ";");

        Scene tabuleiroScene = new Scene(root, 1280, 720);
        stage.setScene(tabuleiroScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}