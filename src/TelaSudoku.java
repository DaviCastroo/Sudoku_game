import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TelaSudoku extends Application {

    // Ícones vetoriais (SVG Paths 24x24 estilo Lucide/Feather - nunca ficam quebrados)
    private static final String SVG_PLAY = "M6 4l14 8-14 8V4z";
    private static final String SVG_SUN = "M12 8a4 4 0 1 0 0 8 4 4 0 0 0 0-8z M12 2v2 M12 20v2 M4.93 4.93l1.41 1.41 M17.66 17.66l1.41 1.41 M2 12h2 M20 12h2 M4.93 19.07l1.41-1.41 M17.66 6.34l1.41-1.41";
    private static final String SVG_MOON = "M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z";
    private static final String SVG_BACK = "M19 12H5 M12 19l-7-7 7-7";
    private static final String SVG_CLOCK = "M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20z M12 6v6l4 2";
    private static final String SVG_PENCIL = "M17 3a2.83 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z";
    private static final String SVG_ERASER = "M20 5H9l-7 7 7 7h11a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2z M18 9l-6 6 M12 9l6 6";
    private static final String SVG_BULB = "M9 18h6 M10 22h4 M12 2a7 7 0 0 0-4 12.7V17h8v-2.3A7 7 0 0 0 12 2z";
    private static final String SVG_REFRESH = "M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8 M3 3v5h5";
    private static final String SVG_SPARKLES = "M12 3l1.9 5.8a2 2 0 0 0 1.3 1.3L21 12l-5.8 1.9a2 2 0 0 0-1.3 1.3L12 21l-1.9-5.8a2 2 0 0 0-1.3-1.3L3 12l5.8-1.9a2 2 0 0 0 1.3-1.3L12 3z";

    private boolean temaEscuro = false;
    private Stage mainStage;
    private Scene mainScene;
    private EventHandler<KeyEvent> filtroTecladoAtual;

    // Estado da partida atual
    private int linhaSelecionada = -1;
    private int colunaSelecionada = -1;
    private boolean modoNotas = false;
    private boolean[][][] notas;
    private boolean[][] celulaEmConflito;
    private int contadorErros = 0;
    private int contadorDicas = 0;
    private boolean jogoFinalizado = false;

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        // Cria uma única Scene para preservar maximização / tela cheia ao trocar de telas
        mainScene = new Scene(new StackPane(), 1280, 760);
        mainStage.setTitle("Sudoku - JavaFX");
        mainStage.setScene(mainScene);
        mostrarTelaInicial();
        mainStage.show();
    }

    /**
     * Cria um ícone vetorial SVG nítido com escala proporcional.
     */
    private StackPane criarIconeSVG(String pathData, double escala, String corHex, boolean preenchido) {
        SVGPath svg = new SVGPath();
        svg.setContent(pathData);
        Color cor = Color.web(corHex);
        if (preenchido) {
            svg.setFill(cor);
            svg.setStroke(cor);
            svg.setStrokeWidth(1.2);
        } else {
            svg.setFill(Color.TRANSPARENT);
            svg.setStroke(cor);
            svg.setStrokeWidth(2.1);
        }
        svg.setStrokeLineCap(StrokeLineCap.ROUND);
        svg.setStrokeLineJoin(StrokeLineJoin.ROUND);
        svg.setScaleX(escala);
        svg.setScaleY(escala);

        StackPane holder = new StackPane(svg);
        double dim = 22 * escala;
        holder.setPrefSize(dim, dim);
        holder.setMinSize(dim, dim);
        holder.setMaxSize(dim, dim);
        return holder;
    }

    // ==================== TELA INICIAL ====================

    private void mostrarTelaInicial() {
        if (filtroTecladoAtual != null) {
            mainScene.removeEventFilter(KeyEvent.KEY_PRESSED, filtroTecladoAtual);
            filtroTecladoAtual = null;
        }

        Label iconeBadge = new Label("9×9");
        iconeBadge.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 20));
        iconeBadge.setAlignment(Pos.CENTER);
        iconeBadge.setPrefSize(64, 64);

        Label titulo = new Label("SUDOKU");
        titulo.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 72));

        Label subtitulo = new Label("Feito por Davi Castro");
        subtitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));

        Button btnNovoJogo = new Button("Novo Jogo");
        btnNovoJogo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 23));
        btnNovoJogo.setPrefSize(310, 64);
        btnNovoJogo.setGraphicTextGap(12);
        btnNovoJogo.setContentDisplay(ContentDisplay.LEFT);
        btnNovoJogo.setCursor(Cursor.HAND);

        Label dificuldadeLabel = new Label("Escolha a dificuldade:");
        dificuldadeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 19));

        Button facilBtn = new Button("Fácil");
        Button medioBtn = new Button("Médio");
        Button dificilBtn = new Button("Difícil");

        for (Button btn : new Button[]{facilBtn, medioBtn, dificilBtn}) {
            btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
            btn.setPrefSize(124, 46);
            btn.setCursor(Cursor.HAND);
        }

        HBox dificuldadeBox = new HBox(16, facilBtn, medioBtn, dificilBtn);
        dificuldadeBox.setAlignment(Pos.CENTER);

        VBox dificuldadeContainer = new VBox(14, dificuldadeLabel, dificuldadeBox);
        dificuldadeContainer.setAlignment(Pos.CENTER);
        dificuldadeContainer.setOpacity(0);

        Label temaLabel = new Label("Tema:");
        temaLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        Button temaBtn = new Button();
        temaBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        temaBtn.setPrefSize(136, 44);
        temaBtn.setGraphicTextGap(9);
        temaBtn.setContentDisplay(ContentDisplay.LEFT);
        temaBtn.setCursor(Cursor.HAND);

        HBox temaBox = new HBox(12, temaLabel, temaBtn);
        temaBox.setAlignment(Pos.CENTER);

        StackPane iconeDica = new StackPane();
        Label dicaAtalhos = new Label("Dica: Use o teclado (1–9, Setas, Backspace e N para Notas) durante o jogo!");
        dicaAtalhos.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 15));
        dicaAtalhos.setWrapText(true);
        dicaAtalhos.setTextAlignment(TextAlignment.CENTER);
        dicaAtalhos.setMinHeight(Region.USE_PREF_SIZE);

        HBox boxDicaRodape = new HBox(8, iconeDica, dicaAtalhos);
        boxDicaRodape.setAlignment(Pos.CENTER);
        boxDicaRodape.setPadding(new Insets(6, 12, 0, 12));

        VBox cardCentral = new VBox(22, iconeBadge, titulo, subtitulo, btnNovoJogo, temaBox, boxDicaRodape);
        cardCentral.setAlignment(Pos.CENTER);
        cardCentral.setPadding(new Insets(44, 56, 44, 56));
        cardCentral.setMaxWidth(720);
        cardCentral.setFillWidth(true);

        StackPane layout = new StackPane(cardCentral);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(24));

        Runnable aplicarTemaMenu = () -> {
            String bgPrincipal = temaEscuro ? "#181a20" : "#f6efe0";
            String bgCard = temaEscuro ? "#22252e" : "#fffdf7";
            String bordaCard = temaEscuro ? "#393e4d" : "#e6d7b2";
            String corPrimaria = temaEscuro ? "#f0c96b" : "#a8842c";
            String corSub = temaEscuro ? "#b5bcc9" : "#6e6249";
            String bgBotao = temaEscuro ? "#2f3441" : "#f7e7b6";
            String bgBotaoPrimario = temaEscuro ? "#d4a843" : "#c79d38";
            String txtBotaoPrimario = temaEscuro ? "#181a20" : "#ffffff";

            layout.setStyle("-fx-background-color: " + bgPrincipal + ";");
            cardCentral.setStyle(
                    "-fx-background-color: " + bgCard + ";" +
                    "-fx-background-radius: 28;" +
                    "-fx-border-color: " + bordaCard + ";" +
                    "-fx-border-radius: 28;" +
                    "-fx-border-width: 2;"
            );
            cardCentral.setEffect(new DropShadow(24, temaEscuro ? Color.rgb(0, 0, 0, 0.55) : Color.rgb(168, 132, 44, 0.18)));

            iconeBadge.setStyle(
                    "-fx-background-color: " + bgBotao + ";" +
                    "-fx-text-fill: " + corPrimaria + ";" +
                    "-fx-background-radius: 18;" +
                    "-fx-border-color: " + corPrimaria + ";" +
                    "-fx-border-radius: 18;" +
                    "-fx-border-width: 2;"
            );

            titulo.setTextFill(Color.web(corPrimaria));
            subtitulo.setTextFill(Color.web(corSub));
            dificuldadeLabel.setTextFill(Color.web(corPrimaria));
            temaLabel.setTextFill(Color.web(corSub));
            dicaAtalhos.setTextFill(Color.web(corSub));

            iconeDica.getChildren().setAll(criarIconeSVG(SVG_BULB, 0.78, corPrimaria, false));

            btnNovoJogo.setGraphic(criarIconeSVG(SVG_PLAY, 0.85, txtBotaoPrimario, true));
            btnNovoJogo.setStyle(
                    "-fx-background-color: " + bgBotaoPrimario + ";" +
                    "-fx-background-radius: 32;" +
                    "-fx-text-fill: " + txtBotaoPrimario + ";"
            );
            btnNovoJogo.setEffect(new DropShadow(12, Color.web(corPrimaria, 0.45)));

            String estiloSecundario =
                    "-fx-background-color: " + bgBotao + ";" +
                    "-fx-background-radius: 22;" +
                    "-fx-text-fill: " + corPrimaria + ";" +
                    "-fx-border-color: " + corPrimaria + ";" +
                    "-fx-border-radius: 22;" +
                    "-fx-border-width: 1.8;";

            for (Button btn : new Button[]{facilBtn, medioBtn, dificilBtn}) {
                btn.setStyle(estiloSecundario);
            }

            temaBtn.setText(temaEscuro ? "Noite" : "Dia");
            temaBtn.setGraphic(criarIconeSVG(temaEscuro ? SVG_MOON : SVG_SUN, 0.80, corPrimaria, false));
            temaBtn.setStyle(estiloSecundario);
        };

        aplicarTemaMenu.run();

        btnNovoJogo.setOnAction(e -> {
            int temaIndex = cardCentral.getChildren().indexOf(temaBox);
            if (!cardCentral.getChildren().contains(dificuldadeContainer)) {
                cardCentral.getChildren().add(temaIndex, dificuldadeContainer);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), dificuldadeContainer);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);
                fadeIn.play();
            } else {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(250), dificuldadeContainer);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(ev -> cardCentral.getChildren().remove(dificuldadeContainer));
                fadeOut.play();
            }
        });

        temaBtn.setOnAction(e -> {
            temaEscuro = !temaEscuro;
            aplicarTemaMenu.run();
        });

        facilBtn.setOnAction(e -> mostrarTabuleiro(mainStage, "fácil"));
        medioBtn.setOnAction(e -> mostrarTabuleiro(mainStage, "médio"));
        dificilBtn.setOnAction(e -> mostrarTabuleiro(mainStage, "difícil"));

        // Troca apenas o root da Scene existente -> mantém tela cheia / maximizado!
        mainScene.setRoot(layout);
    }

    // ==================== TELA DO TABULEIRO ====================

    private void mostrarTabuleiro(Stage stage, String dificuldade) {
        if (filtroTecladoAtual != null) {
            mainScene.removeEventFilter(KeyEvent.KEY_PRESSED, filtroTecladoAtual);
            filtroTecladoAtual = null;
        }

        TabuleiroSudoku tabuleiro = new TabuleiroSudoku();
        tabuleiro.gerarTabuleiroComDificuldade(dificuldade);

        linhaSelecionada = -1;
        colunaSelecionada = -1;
        modoNotas = false;
        notas = new boolean[9][9][9];
        celulaEmConflito = new boolean[9][9];
        contadorErros = 0;
        contadorDicas = 0;
        jogoFinalizado = false;

        Button[][] cells = new Button[9][9];
        Button[] numpadButtons = new Button[9];
        Label[] numpadDigitoLabels = new Label[9];
        Label[] numpadRestanteLabels = new Label[9];

        // --- Barra Superior ---
        Button btnVoltar = new Button("Menu");
        btnVoltar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        btnVoltar.setPrefHeight(42);
        btnVoltar.setPadding(new Insets(0, 18, 0, 14));
        btnVoltar.setGraphicTextGap(8);
        btnVoltar.setCursor(Cursor.HAND);

        String nomeDif = dificuldade.substring(0, 1).toUpperCase() + dificuldade.substring(1);
        Label badgeDificuldade = new Label("Nível: " + nomeDif);
        badgeDificuldade.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        badgeDificuldade.setPadding(new Insets(9, 16, 9, 16));

        Label errosLabel = new Label("Erros: 0");
        errosLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));

        Label timerLabel = new Label("00:00");
        timerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 21));
        timerLabel.setGraphicTextGap(8);

        Button btnTemaJogo = new Button();
        btnTemaJogo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        btnTemaJogo.setPrefHeight(42);
        btnTemaJogo.setPadding(new Insets(0, 16, 0, 14));
        btnTemaJogo.setGraphicTextGap(8);
        btnTemaJogo.setCursor(Cursor.HAND);

        final int[] seconds = {0};
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            if (!jogoFinalizado) {
                seconds[0]++;
                int min = seconds[0] / 60;
                int sec = seconds[0] % 60;
                timerLabel.setText(String.format("%02d:%02d", min, sec));
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        btnVoltar.setOnAction(e -> {
            timeline.stop();
            mostrarTelaInicial();
        });

        Region espacadorEsq = new Region();
        Region espacadorDir = new Region();
        HBox.setHgrow(espacadorEsq, Priority.ALWAYS);
        HBox.setHgrow(espacadorDir, Priority.ALWAYS);

        HBox barraTopo = new HBox(20, btnVoltar, badgeDificuldade, espacadorEsq, errosLabel, timerLabel, espacadorDir, btnTemaJogo);
        barraTopo.setAlignment(Pos.CENTER);
        barraTopo.setPadding(new Insets(18, 40, 10, 40));
        barraTopo.setMaxWidth(1120);

        // --- Grid 9x9 do Sudoku ---
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(0);
        grid.setVgap(0);

        // --- Painel Lateral Direito (Status, Teclado Numérico e Ações) ---
        Label statusLabel = new Label("Clique em uma célula ou use as setas para começar.");
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        statusLabel.setWrapText(true);
        statusLabel.setTextAlignment(TextAlignment.CENTER);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setMinHeight(58);
        statusLabel.setPrefHeight(58);
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setPadding(new Insets(8, 14, 8, 14));

        GridPane numpadGrid = new GridPane();
        numpadGrid.setHgap(10);
        numpadGrid.setVgap(10);
        numpadGrid.setAlignment(Pos.CENTER);

        Button btnNotas = new Button("Notas: OFF");
        Button btnApagar = new Button("Apagar");
        Button btnDica = new Button("Dica");
        Button btnLimpar = new Button("Limpar");
        Button btnResolver = new Button("Resolver");

        for (Button b : new Button[]{btnNotas, btnApagar, btnDica, btnLimpar}) {
            b.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            b.setPrefSize(160, 46);
            b.setGraphicTextGap(8);
            b.setContentDisplay(ContentDisplay.LEFT);
            b.setCursor(Cursor.HAND);
        }
        btnResolver.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        btnResolver.setPrefSize(330, 48);
        btnResolver.setGraphicTextGap(10);
        btnResolver.setContentDisplay(ContentDisplay.LEFT);
        btnResolver.setCursor(Cursor.HAND);

        GridPane acoesGrid = new GridPane();
        acoesGrid.setHgap(10);
        acoesGrid.setVgap(10);
        acoesGrid.setAlignment(Pos.CENTER);
        acoesGrid.add(btnNotas, 0, 0);
        acoesGrid.add(btnApagar, 1, 0);
        acoesGrid.add(btnDica, 0, 1);
        acoesGrid.add(btnLimpar, 1, 1);

        Label atalhosInfo = new Label("Atalhos: [1–9] Número • [N] Notas • [Del] Apagar • [Setas] Mover");
        atalhosInfo.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        atalhosInfo.setWrapText(true);
        atalhosInfo.setMinHeight(Region.USE_PREF_SIZE);
        atalhosInfo.setTextAlignment(TextAlignment.CENTER);
        atalhosInfo.setMaxWidth(Double.MAX_VALUE);
        atalhosInfo.setAlignment(Pos.CENTER);

        VBox painelLateral = new VBox(16, statusLabel, numpadGrid, acoesGrid, btnResolver, atalhosInfo);
        painelLateral.setAlignment(Pos.CENTER);
        painelLateral.setPadding(new Insets(22, 24, 22, 24));
        painelLateral.setPrefWidth(382);
        painelLateral.setMinWidth(382);

        HBox areaPrincipal = new HBox(42, grid, painelLateral);
        areaPrincipal.setAlignment(Pos.CENTER);
        areaPrincipal.setPadding(new Insets(10, 30, 25, 30));

        BorderPane root = new BorderPane();
        BorderPane.setAlignment(barraTopo, Pos.CENTER);
        root.setTop(barraTopo);
        root.setCenter(areaPrincipal);

        // Criação dos 9 botões do teclado numérico (Numpad) com layout rico em duas linhas
        for (int n = 1; n <= 9; n++) {
            Label lblDigito = new Label(String.valueOf(n));
            lblDigito.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));
            lblDigito.setAlignment(Pos.CENTER);

            Label lblRest = new Label("9 rest.");
            lblRest.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
            lblRest.setAlignment(Pos.CENTER);

            VBox conteudoNumpad = new VBox(1, lblDigito, lblRest);
            conteudoNumpad.setAlignment(Pos.CENTER);

            Button nb = new Button();
            nb.setGraphic(conteudoNumpad);
            nb.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            nb.setPrefSize(103, 64);
            nb.setCursor(Cursor.HAND);
            nb.setFocusTraversable(false);

            numpadDigitoLabels[n - 1] = lblDigito;
            numpadRestanteLabels[n - 1] = lblRest;
            numpadButtons[n - 1] = nb;
            numpadGrid.add(nb, (n - 1) % 3, (n - 1) / 3);
        }

        // --- Função de Atualização Visual Completa (Tabuleiro + Tema + Ícones Vetoriais) ---
        Runnable atualizarVisual = () -> {
            String corFundo = temaEscuro ? "#181a20" : "#f6efe0";
            String corPainel = temaEscuro ? "#22252e" : "#fffdf7";
            String bordaPainel = temaEscuro ? "#363b4a" : "#e2d3ae";
            String corPrimaria = temaEscuro ? "#f0c96b" : "#9c7822";
            String corSecundaria = temaEscuro ? "#b5bcc9" : "#7c6e54";

            // Cores das células do tabuleiro
            String bgCelulaFixa = temaEscuro ? "#2b303c" : "#ede4ce";
            String bgCelulaEditavel = temaEscuro ? "#20232b" : "#fffdf8";
            String bgCelulaRelacionada = temaEscuro ? "#283244" : "#f5ebd1";
            String bgCelulaMesmoNumero = temaEscuro ? "#3c4d6b" : "#dfca92";
            String bgCelulaSelecionada = temaEscuro ? "#4a5f82" : "#f2d98d";
            String bgCelulaConflito = temaEscuro ? "#6e262e" : "#ffd6d6";

            String txtCelulaFixa = temaEscuro ? "#f2f4f8" : "#1f1d18";
            String txtCelulaUsuario = temaEscuro ? "#68b5ff" : "#1c5fb8";
            String txtCelulaNotas = temaEscuro ? "#9aa4b8" : "#786e58";
            String txtCelulaConflito = temaEscuro ? "#ff8a93" : "#c91c24";
            String corBordaBloco = temaEscuro ? "#8b95a7" : "#2b261c";

            root.setStyle("-fx-background-color: " + corFundo + ";");

            painelLateral.setStyle(
                    "-fx-background-color: " + corPainel + ";" +
                    "-fx-background-radius: 22;" +
                    "-fx-border-color: " + bordaPainel + ";" +
                    "-fx-border-radius: 22;" +
                    "-fx-border-width: 1.5;"
            );
            painelLateral.setEffect(new DropShadow(18, temaEscuro ? Color.rgb(0, 0, 0, 0.45) : Color.rgb(156, 120, 34, 0.14)));

            grid.setEffect(new DropShadow(20, temaEscuro ? Color.rgb(0, 0, 0, 0.55) : Color.rgb(0, 0, 0, 0.15)));

            String estiloBotaoTopo =
                    "-fx-background-color: " + corPainel + ";" +
                    "-fx-background-radius: 14;" +
                    "-fx-text-fill: " + corPrimaria + ";" +
                    "-fx-border-color: " + bordaPainel + ";" +
                    "-fx-border-radius: 14;" +
                    "-fx-border-width: 1.5;";

            btnVoltar.setGraphic(criarIconeSVG(SVG_BACK, 0.78, corPrimaria, false));
            btnVoltar.setStyle(estiloBotaoTopo);

            btnTemaJogo.setText(temaEscuro ? "Noite" : "Dia");
            btnTemaJogo.setGraphic(criarIconeSVG(temaEscuro ? SVG_MOON : SVG_SUN, 0.78, corPrimaria, false));
            btnTemaJogo.setStyle(estiloBotaoTopo);

            badgeDificuldade.setStyle(
                    "-fx-background-color: " + corPainel + ";" +
                    "-fx-background-radius: 14;" +
                    "-fx-text-fill: " + corPrimaria + ";" +
                    "-fx-border-color: " + bordaPainel + ";" +
                    "-fx-border-radius: 14;" +
                    "-fx-border-width: 1.5;"
            );

            errosLabel.setText("Erros: " + contadorErros);
            errosLabel.setTextFill(contadorErros > 0 ? Color.web(txtCelulaConflito) : Color.web(corSecundaria));

            timerLabel.setGraphic(criarIconeSVG(SVG_CLOCK, 0.82, corPrimaria, false));
            timerLabel.setTextFill(Color.web(corPrimaria));

            atalhosInfo.setTextFill(Color.web(corSecundaria));

            statusLabel.setStyle(
                    "-fx-background-color: " + (temaEscuro ? "#1b1e26" : "#f7f0df") + ";" +
                    "-fx-background-radius: 12;" +
                    "-fx-text-fill: " + corPrimaria + ";"
            );

            Integer valorSelecionado = (linhaSelecionada >= 0 && colunaSelecionada >= 0)
                    ? tabuleiro.getValor(linhaSelecionada, colunaSelecionada)
                    : null;

            int[] contagemNumeros = new int[10];
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    Integer v = tabuleiro.getValor(r, c);
                    if (v != null && v >= 1 && v <= 9) {
                        contagemNumeros[v]++;
                    }
                }
            }

            // Atualiza cada célula do tabuleiro 9x9
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    Button cell = cells[i][j];
                    Integer valor = tabuleiro.getValor(i, j);
                    boolean fixa = tabuleiro.isFixa(i, j);
                    boolean selecionada = (i == linhaSelecionada && j == colunaSelecionada);
                    boolean mesmaLinhaColunaOuBloco = (linhaSelecionada >= 0 && colunaSelecionada >= 0) &&
                            (i == linhaSelecionada || j == colunaSelecionada ||
                                    (i / 3 == linhaSelecionada / 3 && j / 3 == colunaSelecionada / 3));
                    boolean mesmoNumero = (valor != null && valorSelecionado != null && valor.equals(valorSelecionado));
                    boolean conflito = celulaEmConflito[i][j];

                    int top = (i == 0) ? 3 : (i % 3 == 0 ? 2 : 1);
                    int left = (j == 0) ? 3 : (j % 3 == 0 ? 2 : 1);
                    int right = (j == 8) ? 3 : (j % 3 == 2 ? 2 : 1);
                    int bottom = (i == 8) ? 3 : (i % 3 == 2 ? 2 : 1);

                    String bg;
                    if (conflito) {
                        bg = bgCelulaConflito;
                    } else if (selecionada) {
                        bg = bgCelulaSelecionada;
                    } else if (mesmoNumero) {
                        bg = bgCelulaMesmoNumero;
                    } else if (mesmaLinhaColunaOuBloco) {
                        bg = bgCelulaRelacionada;
                    } else if (fixa) {
                        bg = bgCelulaFixa;
                    } else {
                        bg = bgCelulaEditavel;
                    }

                    if (valor != null) {
                        cell.setText(valor.toString());
                        cell.setFont(Font.font("Segoe UI", fixa ? FontWeight.EXTRA_BOLD : FontWeight.BOLD, 27));
                        String txtColor = conflito ? txtCelulaConflito : (fixa ? txtCelulaFixa : txtCelulaUsuario);
                        cell.setStyle(String.format(
                                "-fx-background-color: %s;" +
                                "-fx-border-color: %s;" +
                                "-fx-border-width: %d %d %d %d;" +
                                "-fx-text-fill: %s;" +
                                "-fx-background-insets: 0;" +
                                "-fx-padding: 0;",
                                bg, corBordaBloco, top, right, bottom, left, txtColor
                        ));
                    } else {
                        String textoNotas = formatarNotasCelula(i, j);
                        cell.setText(textoNotas);
                        cell.setFont(Font.font("Consolas", FontWeight.BOLD, 11));
                        cell.setStyle(String.format(
                                "-fx-background-color: %s;" +
                                "-fx-border-color: %s;" +
                                "-fx-border-width: %d %d %d %d;" +
                                "-fx-text-fill: %s;" +
                                "-fx-background-insets: 0;" +
                                "-fx-padding: 1;",
                                bg, corBordaBloco, top, right, bottom, left, txtCelulaNotas
                        ));
                    }
                }
            }

            // Atualiza os botões do teclado numérico (1 a 9)
            for (int n = 1; n <= 9; n++) {
                Button nb = numpadButtons[n - 1];
                int restantes = Math.max(0, 9 - contagemNumeros[n]);
                boolean completo = restantes == 0;

                String bgNumpad = completo
                        ? (temaEscuro ? "#1d2028" : "#ebe4d4")
                        : (temaEscuro ? "#2c313f" : "#f7e7b6");
                String txtNumpad = completo
                        ? (temaEscuro ? "#586070" : "#b0a690")
                        : corPrimaria;
                String txtSubNumpad = completo
                        ? (temaEscuro ? "#49505e" : "#bdb39d")
                        : corSecundaria;

                numpadDigitoLabels[n - 1].setTextFill(Color.web(txtNumpad));
                numpadRestanteLabels[n - 1].setText(completo ? "completo" : restantes + " rest.");
                numpadRestanteLabels[n - 1].setTextFill(Color.web(txtSubNumpad));

                nb.setStyle(
                        "-fx-background-color: " + bgNumpad + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + (completo ? "transparent" : bordaPainel) + ";" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1.5;"
                );
            }

            // Estilo e ícones vetoriais dos botões de ação
            String estiloAcao =
                    "-fx-background-color: " + (temaEscuro ? "#2b303d" : "#f3ead3") + ";" +
                    "-fx-background-radius: 13;" +
                    "-fx-text-fill: " + corPrimaria + ";" +
                    "-fx-border-color: " + bordaPainel + ";" +
                    "-fx-border-radius: 13;" +
                    "-fx-border-width: 1.3;";

            btnApagar.setGraphic(criarIconeSVG(SVG_ERASER, 0.78, corPrimaria, false));
            btnApagar.setStyle(estiloAcao);

            btnDica.setGraphic(criarIconeSVG(SVG_BULB, 0.78, corPrimaria, false));
            btnDica.setStyle(estiloAcao);

            btnLimpar.setGraphic(criarIconeSVG(SVG_REFRESH, 0.78, corPrimaria, false));
            btnLimpar.setStyle(estiloAcao);

            btnNotas.setText(modoNotas ? "Notas: ON" : "Notas: OFF");
            if (modoNotas) {
                btnNotas.setGraphic(criarIconeSVG(SVG_PENCIL, 0.78, "#ffffff", false));
                btnNotas.setStyle(
                        "-fx-background-color: " + (temaEscuro ? "#3b5bdb" : "#2b6cb0") + ";" +
                        "-fx-background-radius: 13;" +
                        "-fx-text-fill: #ffffff;"
                );
            } else {
                btnNotas.setGraphic(criarIconeSVG(SVG_PENCIL, 0.78, corPrimaria, false));
                btnNotas.setStyle(estiloAcao);
            }

            String corTxtResolver = temaEscuro ? "#181a20" : "#ffffff";
            btnResolver.setGraphic(criarIconeSVG(SVG_SPARKLES, 0.82, corTxtResolver, false));
            btnResolver.setStyle(
                    "-fx-background-color: " + (temaEscuro ? "#d4a843" : "#c79d38") + ";" +
                    "-fx-background-radius: 14;" +
                    "-fx-text-fill: " + corTxtResolver + ";"
            );
        };

        // --- Checagem de Vitória ---
        Runnable checarVitoria = () -> {
            if (jogoFinalizado) return;
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (tabuleiro.getValor(r, c) == null) return;
                }
            }
            if (tabuleiro.validarTabuleiroParalelo()) {
                jogoFinalizado = true;
                timeline.stop();
                statusLabel.setText("Parabéns! Tabuleiro concluído com sucesso!");
                Alert win = new Alert(Alert.AlertType.INFORMATION);
                win.setTitle("Vitória no Sudoku!");
                win.setHeaderText("Parabéns, você venceu!");
                win.setContentText(String.format(
                        "Dificuldade: %s\nTempo: %s\nErros: %d\nDicas usadas: %d",
                        nomeDif,
                        timerLabel.getText(),
                        contadorErros,
                        contadorDicas
                ));
                win.showAndWait();
                mostrarTelaInicial();
            }
        };

        // --- Função para Inserir Número (Teclado Físico ou Numpad da Tela) ---
        java.util.function.IntConsumer inserirNumero = (num) -> {
            if (jogoFinalizado) return;
            if (linhaSelecionada < 0 || colunaSelecionada < 0) {
                statusLabel.setText("Selecione uma célula do tabuleiro primeiro!");
                return;
            }
            if (tabuleiro.isFixa(linhaSelecionada, colunaSelecionada)) {
                statusLabel.setText("Esta célula é fixa do desafio e não pode ser alterada.");
                return;
            }

            limparConflitos();

            if (modoNotas) {
                if (tabuleiro.getValor(linhaSelecionada, colunaSelecionada) != null) {
                    tabuleiro.setarValorTabuleiro(linhaSelecionada, colunaSelecionada, null);
                }
                notas[linhaSelecionada][colunaSelecionada][num - 1] = !notas[linhaSelecionada][colunaSelecionada][num - 1];
                statusLabel.setText("Nota " + num + " atualizada na linha " + (linhaSelecionada + 1) + ", col " + (colunaSelecionada + 1));
                atualizarVisual.run();
                return;
            }

            if (tabuleiro.validarJogada(linhaSelecionada, colunaSelecionada, num)) {
                tabuleiro.setarValorTabuleiro(linhaSelecionada, colunaSelecionada, num);
                for (int k = 0; k < 9; k++) {
                    notas[linhaSelecionada][colunaSelecionada][k] = false;
                    notas[linhaSelecionada][k][num - 1] = false;
                    notas[k][colunaSelecionada][num - 1] = false;
                }
                int bi = (linhaSelecionada / 3) * 3;
                int bj = (colunaSelecionada / 3) * 3;
                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 3; c++) {
                        notas[bi + r][bj + c][num - 1] = false;
                    }
                }
                statusLabel.setText("Número " + num + " inserido em (" + (linhaSelecionada + 1) + ", " + (colunaSelecionada + 1) + ")");
                atualizarVisual.run();
                checarVitoria.run();
            } else {
                contadorErros++;
                marcarConflitos(tabuleiro, linhaSelecionada, colunaSelecionada, num);
                statusLabel.setText("Jogada inválida! O número " + num + " já existe na linha, coluna ou bloco.");
                atualizarVisual.run();
                animarErroCelula(cells[linhaSelecionada][colunaSelecionada]);

                PauseTransition pause = new PauseTransition(Duration.millis(1200));
                pause.setOnFinished(ev -> {
                    limparConflitos();
                    atualizarVisual.run();
                });
                pause.play();
            }
        };

        // Associa clique nos botões do Numpad
        for (int n = 1; n <= 9; n++) {
            final int valorNum = n;
            numpadButtons[n - 1].setOnAction(e -> inserirNumero.accept(valorNum));
        }

        // --- Função para Apagar Célula ---
        Runnable apagarCelulaSelecionada = () -> {
            if (jogoFinalizado || linhaSelecionada < 0 || colunaSelecionada < 0) return;
            if (tabuleiro.isFixa(linhaSelecionada, colunaSelecionada)) {
                statusLabel.setText("Células fixas não podem ser apagadas.");
                return;
            }
            tabuleiro.setarValorTabuleiro(linhaSelecionada, colunaSelecionada, null);
            for (int k = 0; k < 9; k++) {
                notas[linhaSelecionada][colunaSelecionada][k] = false;
            }
            limparConflitos();
            statusLabel.setText("Célula (" + (linhaSelecionada + 1) + ", " + (colunaSelecionada + 1) + ") limpa.");
            atualizarVisual.run();
        };

        // Criação das 81 células do Grid
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Button cell = new Button();
                cell.setPrefSize(62, 62);
                cell.setMinSize(62, 62);
                cell.setMaxSize(62, 62);
                cell.setCursor(Cursor.HAND);
                cell.setFocusTraversable(false);

                final int r = i;
                final int c = j;
                cell.setOnAction(e -> {
                    linhaSelecionada = r;
                    colunaSelecionada = c;
                    limparConflitos();
                    if (tabuleiro.isFixa(r, c)) {
                        statusLabel.setText("Célula fixa (" + (r + 1) + ", " + (c + 1) + ") — valor " + tabuleiro.getValor(r, c));
                    } else {
                        statusLabel.setText("Célula (" + (r + 1) + ", " + (c + 1) + ") selecionada. Digite de 1 a 9.");
                    }
                    atualizarVisual.run();
                });

                cells[i][j] = cell;
                grid.add(cell, j, i);
            }
        }

        // --- Ações dos Botões Laterais ---
        btnNotas.setFocusTraversable(false);
        btnNotas.setOnAction(e -> {
            modoNotas = !modoNotas;
            statusLabel.setText(modoNotas
                    ? "Modo Notas ATIVADO: digite números para anotar rascunhos."
                    : "Modo Notas DESATIVADO.");
            atualizarVisual.run();
        });

        btnApagar.setFocusTraversable(false);
        btnApagar.setOnAction(e -> apagarCelulaSelecionada.run());

        btnDica.setFocusTraversable(false);
        btnDica.setOnAction(e -> {
            if (jogoFinalizado) return;
            int rAlvo = -1;
            int cAlvo = -1;

            if (linhaSelecionada >= 0 && colunaSelecionada >= 0 &&
                    !tabuleiro.isFixa(linhaSelecionada, colunaSelecionada) &&
                    !Integer.valueOf(tabuleiro.getValorSolucao(linhaSelecionada, colunaSelecionada))
                            .equals(tabuleiro.getValor(linhaSelecionada, colunaSelecionada))) {
                rAlvo = linhaSelecionada;
                cAlvo = colunaSelecionada;
            } else {
                List<int[]> vazias = new ArrayList<>();
                for (int r = 0; r < 9; r++) {
                    for (int c = 0; c < 9; c++) {
                        if (tabuleiro.getValor(r, c) == null) {
                            vazias.add(new int[]{r, c});
                        }
                    }
                }
                if (!vazias.isEmpty()) {
                    int[] escolhida = vazias.get(new Random().nextInt(vazias.size()));
                    rAlvo = escolhida[0];
                    cAlvo = escolhida[1];
                }
            }

            if (rAlvo >= 0 && cAlvo >= 0) {
                Integer correto = tabuleiro.getValorSolucao(rAlvo, cAlvo);
                tabuleiro.setarValorTabuleiro(rAlvo, cAlvo, correto);
                for (int k = 0; k < 9; k++) notas[rAlvo][cAlvo][k] = false;
                linhaSelecionada = rAlvo;
                colunaSelecionada = cAlvo;
                contadorDicas++;
                statusLabel.setText("Dica revelada em (" + (rAlvo + 1) + ", " + (cAlvo + 1) + "): número " + correto);
                atualizarVisual.run();
                checarVitoria.run();
            } else {
                statusLabel.setText("Todas as células já estão preenchidas!");
            }
        });

        btnLimpar.setFocusTraversable(false);
        btnLimpar.setOnAction(e -> {
            if (jogoFinalizado) return;
            tabuleiro.limparJogadas();
            notas = new boolean[9][9][9];
            limparConflitos();
            statusLabel.setText("Tabuleiro reiniciado para a configuração inicial.");
            atualizarVisual.run();
        });

        btnResolver.setFocusTraversable(false);
        btnResolver.setOnAction(e -> {
            if (jogoFinalizado) return;
            tabuleiro.resolverTabuleiro();
            notas = new boolean[9][9][9];
            limparConflitos();
            jogoFinalizado = true;
            timeline.stop();
            statusLabel.setText("Solução completa exibida no tabuleiro!");
            atualizarVisual.run();
        });

        btnTemaJogo.setFocusTraversable(false);
        btnTemaJogo.setOnAction(e -> {
            temaEscuro = !temaEscuro;
            atualizarVisual.run();
        });

        atualizarVisual.run();

        // --- Eventos de Teclado registrados na mainScene ---
        filtroTecladoAtual = ev -> {
            KeyCode code = ev.getCode();

            if (code == KeyCode.N) {
                btnNotas.fire();
                ev.consume();
                return;
            }

            if (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE || code == KeyCode.DIGIT0 || code == KeyCode.NUMPAD0) {
                apagarCelulaSelecionada.run();
                ev.consume();
                return;
            }

            if (code == KeyCode.UP || code == KeyCode.W) {
                linhaSelecionada = (linhaSelecionada <= 0) ? 8 : linhaSelecionada - 1;
                if (colunaSelecionada < 0) colunaSelecionada = 0;
                limparConflitos();
                atualizarVisual.run();
                ev.consume();
                return;
            }
            if (code == KeyCode.DOWN || code == KeyCode.S) {
                linhaSelecionada = (linhaSelecionada < 0 || linhaSelecionada >= 8) ? 0 : linhaSelecionada + 1;
                if (colunaSelecionada < 0) colunaSelecionada = 0;
                limparConflitos();
                atualizarVisual.run();
                ev.consume();
                return;
            }
            if (code == KeyCode.LEFT || code == KeyCode.A) {
                colunaSelecionada = (colunaSelecionada <= 0) ? 8 : colunaSelecionada - 1;
                if (linhaSelecionada < 0) linhaSelecionada = 0;
                limparConflitos();
                atualizarVisual.run();
                ev.consume();
                return;
            }
            if (code == KeyCode.RIGHT || code == KeyCode.D) {
                colunaSelecionada = (colunaSelecionada < 0 || colunaSelecionada >= 8) ? 0 : colunaSelecionada + 1;
                if (linhaSelecionada < 0) linhaSelecionada = 0;
                limparConflitos();
                atualizarVisual.run();
                ev.consume();
                return;
            }

            int num = -1;
            if (code.isDigitKey()) {
                String txt = ev.getText();
                if (txt != null && txt.length() == 1 && txt.charAt(0) >= '1' && txt.charAt(0) <= '9') {
                    num = txt.charAt(0) - '0';
                }
            }
            if (num >= 1 && num <= 9) {
                inserirNumero.accept(num);
                ev.consume();
            }
        };
        mainScene.addEventFilter(KeyEvent.KEY_PRESSED, filtroTecladoAtual);

        // Troca apenas o root da Scene existente -> mantém tela cheia / maximizado!
        mainScene.setRoot(root);
    }

    private String formatarNotasCelula(int linha, int coluna) {
        boolean temAlguma = false;
        for (int k = 0; k < 9; k++) {
            if (notas[linha][coluna][k]) {
                temAlguma = true;
                break;
            }
        }
        if (!temAlguma) return "";

        StringBuilder sb = new StringBuilder();
        for (int n = 1; n <= 9; n++) {
            sb.append(notas[linha][coluna][n - 1] ? String.valueOf(n) : " ");
            if (n % 3 == 0 && n < 9) {
                sb.append("\n");
            } else if (n % 3 != 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private void limparConflitos() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                celulaEmConflito[i][j] = false;
            }
        }
    }

    private void marcarConflitos(TabuleiroSudoku tabuleiro, int linha, int coluna, int valor) {
        celulaEmConflito[linha][coluna] = true;
        for (int i = 0; i < 9; i++) {
            if (i != linha && Integer.valueOf(valor).equals(tabuleiro.getValor(i, coluna))) {
                celulaEmConflito[i][coluna] = true;
            }
        }
        for (int j = 0; j < 9; j++) {
            if (j != coluna && Integer.valueOf(valor).equals(tabuleiro.getValor(linha, j))) {
                celulaEmConflito[linha][j] = true;
            }
        }
        int inicioLinha = (linha / 3) * 3;
        int inicioColuna = (coluna / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int r = inicioLinha + i;
                int c = inicioColuna + j;
                if ((r != linha || c != coluna) && Integer.valueOf(valor).equals(tabuleiro.getValor(r, c))) {
                    celulaEmConflito[r][c] = true;
                }
            }
        }
    }

    private void animarErroCelula(Button cell) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(55), cell);
        tt.setFromX(-5);
        tt.setToX(5);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> cell.setTranslateX(0));
        tt.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
