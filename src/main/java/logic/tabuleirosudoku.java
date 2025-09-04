package logic;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class TabuleiroSudoku {
    private boolean fixas[][];
    private Integer[][] tabuleiro;

    // construtor:
    public TabuleiroSudoku() {
        tabuleiro = new Integer[9][9]; //objeto
        fixas = new boolean[9][9];
    }

    public void setarValorTabuleiro(int linha, int coluna, int valor) {
        tabuleiro[linha][coluna] = valor;
    }

    public void imprimirTabuleiro() {
        for (int linha = 0; linha < 9; linha++) {
            for (int coluna = 0; coluna < 9; coluna++) {
                System.out.print(tabuleiro[linha][coluna] + " ");
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

    // Método para validar todas as linhas em paralelo
    public boolean validarLinhasParalelo() {
        boolean[] resultadoLinhas = new boolean[9];
        Semoaphre semaforo = new Semaphore(1);
        Thread[] threads = new Thread[9];

        for (int i = 0; i < 9; i++) {
            threads[i] = new ValidadorLinhaSudoku(i, resultadoLinhas, semaforo);
            threads[i].start();
        }
        for (int i = 0; i < 9; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Retorna true se todas as linhas forem válidas
        for (boolean ok : resultadoLinhas) {
            if (!ok) return false;
        }
        return true;
    }
}