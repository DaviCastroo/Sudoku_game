import java.util.Random;
import java.util.concurrent.Semaphore;

public class TabuleiroSudoku {
    private boolean fixas[][];
    private Integer[][] tabuleiro;
    private Random random = new Random();

    public TabuleiroSudoku() {
        tabuleiro = new Integer[9][9];
        fixas = new boolean[9][9];
    }

    // Aceita Null
    public void setarValorTabuleiro(int linha, int coluna, Integer valor) {
        tabuleiro[linha][coluna] = valor;
    }

    public Integer getValor(int linha, int coluna) {
        return tabuleiro[linha][coluna];
    }

    public boolean isFixa(int linha, int coluna) {
        return fixas[linha][coluna];
    }

    public void imprimirTabuleiro() {
        for (int linha = 0; linha < 9; linha++) {
            for (int coluna = 0; coluna < 9; coluna++) {
                if (tabuleiro[linha][coluna] == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(tabuleiro[linha][coluna] + " ");
                }
            }
            System.out.println();
        }
    }

    public boolean validarJogada(int linha, int coluna, int valor){
        //verifica linha
        for (int i = 0; i < 9; i++){
            if(tabuleiro[i][coluna] != null && tabuleiro[i][coluna] == valor) return false;
        }
        //verifica coluna
        for (int j = 0; j < 9; j++){
            if(tabuleiro[linha][j] != null && tabuleiro[linha][j] == valor ) return false;
        }
        //verifica quadrante
        int inicioLinha = (linha / 3) * 3;
        int inicioColuna = (coluna / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tabuleiro[inicioLinha + i][inicioColuna + j] != null &&
                        tabuleiro[inicioLinha + i][inicioColuna + j] == valor) return false;
            }
        }
        return true;
    }

    public boolean adicionarNumero(int linha, int coluna, int valor){
        if (fixas[linha][coluna]) return false; // bloqueia alteração das casas fixas
        if(valor > 9 || valor < 1) return false; // valor fora do padrão
        if(tabuleiro[linha][coluna] != null ) return false; // ja existe um valor na posição
        if(!validarJogada(linha, coluna, valor)) return false;
        tabuleiro[linha][coluna] = valor;
        return true; // adição sucedida
    }

    // Método para gerar o tabuleiro de acordo com a dificuldade, garantindo solução única
    public void gerarTabuleiroComDificuldade(String dificuldade) {
        preencherTabuleiro(0, 0);

        int casasParaRemover;
        switch (dificuldade.toLowerCase()) {
            case "fácil":
                casasParaRemover = 35;
                break;
            case "médio":
                casasParaRemover = 45;
                break;
            case "difícil":
                casasParaRemover = 55;
                break;
            default:
                casasParaRemover = 45;
        }

        int removidos = 0;
        int tentativas = 0;
        while (removidos < casasParaRemover && tentativas < 1000) {
            int linha = random.nextInt(9);
            int coluna = random.nextInt(9);
            if (tabuleiro[linha][coluna] != null) {
                int backup = tabuleiro[linha][coluna];
                tabuleiro[linha][coluna] = null;
                fixas[linha][coluna] = false;

                // Verifica se o tabuleiro ainda tem solução única
                if (temUnicaSolucao()) {
                    removidos++;
                } else {
                    tabuleiro[linha][coluna] = backup;
                    fixas[linha][coluna] = true;
                }
                tentativas++;
            }
        }

        // Marca as casas restantes como fixas
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                fixas[i][j] = tabuleiro[i][j] != null;
            }
        }
    }

    // Preenche o tabuleiro usando backtracking
    private boolean preencherTabuleiro(int linha, int coluna) {
        if (linha == 9) return true;
        int proximaLinha = (coluna == 8) ? linha + 1 : linha;
        int proximaColuna = (coluna + 1) % 9;

        int[] numeros = embaralharNumeros();
        for (int num : numeros) {
            if (validarJogada(linha, coluna, num)) {
                tabuleiro[linha][coluna] = num;
                if (preencherTabuleiro(proximaLinha, proximaColuna)) return true;
                tabuleiro[linha][coluna] = null;
            }
        }
        return false;
    }

    // Embaralha os números de 1 a 9
    private int[] embaralharNumeros() {
        int[] numeros = new int[9];
        for (int i = 0; i < 9; i++) numeros[i] = i + 1;
        for (int i = 0; i < 9; i++) {
            int j = random.nextInt(9);
            int temp = numeros[i];
            numeros[i] = numeros[j];
            numeros[j] = temp;
        }
        return numeros;
    }

    // ---- Validação paralela das linhas, colunas e blocos ----

    // Classe interna para validar uma linha usando thread
    private class ValidadorLinhaSudoku extends Thread {
        private int linha;
        private boolean[] resultado;
        private Semaphore semaforo;

        public ValidadorLinhaSudoku(int linha, boolean[] resultado, Semaphore semaforo) {
            this.linha = linha;
            this.resultado = resultado;
            this.semaforo = semaforo;
        }

        @Override
        public void run() {
            boolean[] vistos = new boolean[9];
            for (int j = 0; j < 9; j++) {
                Integer valor = tabuleiro[linha][j];
                if (valor == null || valor < 1 || valor > 9 || vistos[valor - 1]) {
                    try {
                        semaforo.acquire();
                        resultado[linha] = false;
                        semaforo.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                vistos[valor - 1] = true;
            }
            try {
                semaforo.acquire();
                resultado[linha] = true;
                semaforo.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Classe interna para validar uma coluna usando thread
    private class ValidadorColunaSudoku extends Thread {
        private int coluna;
        private boolean[] resultado;
        private Semaphore semaforo;

        public ValidadorColunaSudoku(int coluna, boolean[] resultado, Semaphore semaforo) {
            this.coluna = coluna;
            this.resultado = resultado;
            this.semaforo = semaforo;
        }

        @Override
        public void run() {
            boolean[] vistos = new boolean[9];
            for (int i = 0; i < 9; i++) {
                Integer valor = tabuleiro[i][coluna];
                if (valor == null || valor < 1 || valor > 9 || vistos[valor - 1]) {
                    try {
                        semaforo.acquire();
                        resultado[coluna] = false;
                        semaforo.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                vistos[valor - 1] = true;
            }
            try {
                semaforo.acquire();
                resultado[coluna] = true;
                semaforo.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Classe interna para validar um bloco 3x3 usando thread
    private class ValidadorBlocoSudoku extends Thread {
        private int bloco;
        private boolean[] resultado;
        private Semaphore semaforo;

        public ValidadorBlocoSudoku(int bloco, boolean[] resultado, Semaphore semaforo) {
            this.bloco = bloco;
            this.resultado = resultado;
            this.semaforo = semaforo;
        }

        @Override
        public void run() {
            boolean[] vistos = new boolean[9];
            int inicioLinha = (bloco / 3) * 3;
            int inicioColuna = (bloco % 3) * 3;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Integer valor = tabuleiro[inicioLinha + i][inicioColuna + j];
                    if (valor == null || valor < 1 || valor > 9 || vistos[valor - 1]) {
                        try {
                            semaforo.acquire();
                            resultado[bloco] = false;
                            semaforo.release();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    vistos[valor - 1] = true;
                }
            }
            try {
                semaforo.acquire();
                resultado[bloco] = true;
                semaforo.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Valida linhas, colunas e blocos em paralelo
    public boolean validarTabuleiroParalelo() {
        boolean[] resultadoLinhas = new boolean[9];
        boolean[] resultadoColunas = new boolean[9];
        boolean[] resultadoBlocos = new boolean[9];
        Semaphore semaforo = new Semaphore(1);

        Thread[] threads = new Thread[27];
        for (int i = 0; i < 9; i++) {
            threads[i] = new ValidadorLinhaSudoku(i, resultadoLinhas, semaforo);
            threads[9 + i] = new ValidadorColunaSudoku(i, resultadoColunas, semaforo);
            threads[18 + i] = new ValidadorBlocoSudoku(i, resultadoBlocos, semaforo);
        }
        for (int i = 0; i < 27; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 27; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 9; i++) {
            if (!resultadoLinhas[i] || !resultadoColunas[i] || !resultadoBlocos[i]) return false;
        }
        return true;
    }

    // ---- Verificação de solução única ----

    // Conta o número de soluções possíveis para o tabuleiro atual
    private int contarSolucoes() {
        Integer[][] copia = new Integer[9][9];
        for (int i = 0; i < 9; i++)
            System.arraycopy(tabuleiro[i], 0, copia[i], 0, 9);
        return contarSolucoesAux(copia, 0, 0, 0, 2); // Para de contar se achar mais de 1 solução
    }

    // Backtracking para contar soluções
    private int contarSolucoesAux(Integer[][] board, int linha, int coluna, int count, int limite) {
        if (linha == 9) return count + 1;
        if (board[linha][coluna] != null) {
            int proximaLinha = (coluna == 8) ? linha + 1 : linha;
            int proximaColuna = (coluna + 1) % 9;
            return contarSolucoesAux(board, proximaLinha, proximaColuna, count, limite);
        }
        for (int num = 1; num <= 9; num++) {
            if (validarJogadaTabuleiro(board, linha, coluna, num)) {
                board[linha][coluna] = num;
                int proximaLinha = (coluna == 8) ? linha + 1 : linha;
                int proximaColuna = (coluna + 1) % 9;
                count = contarSolucoesAux(board, proximaLinha, proximaColuna, count, limite);
                if (count >= limite) return count;
                board[linha][coluna] = null;
            }
        }
        return count;
    }

    // Valida jogada para um tabuleiro genérico (usado na contagem de soluções)
    private boolean validarJogadaTabuleiro(Integer[][] board, int linha, int coluna, int valor) {
        for (int i = 0; i < 9; i++) {
            if (board[i][coluna] != null && board[i][coluna] == valor) return false;
        }
        for (int j = 0; j < 9; j++) {
            if (board[linha][j] != null && board[linha][j] == valor) return false;
        }
        int inicioLinha = (linha / 3) * 3;
        int inicioColuna = (coluna / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[inicioLinha + i][inicioColuna + j] != null &&
                        board[inicioLinha + i][inicioColuna + j] == valor) return false;
            }
        }
        return true;
    }

    // Retorna true se o tabuleiro tem solução única
    private boolean temUnicaSolucao() {
        return contarSolucoes() == 1;
    }
}