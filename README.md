# Sudoku em JavaFX

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-17-4285F4?style=for-the-badge&logo=java&logoColor=white)
![License](https://img.shields.io/badge/Licen%C3%A7a-MIT-green?style=for-the-badge)

Aplicação desktop completa de **Sudoku** desenvolvida em **Java** com interface gráfica moderna em **JavaFX**. O projeto combina geração procedural de tabuleiros com garantia de solução única via *backtracking* e validação concorrente utilizando **27 threads sincronizadas por semáforos**.

---

## Principais Funcionalidades

- **Geração Inteligente com Solução Única**:
  - Tabuleiros gerados dinamicamente por *backtracking* aleatório.
  - Três níveis de dificuldade (**Fácil**, **Médio** e **Difícil**), garantindo matematicamente que cada desafio possua uma única solução válida.
- **Validação Paralela (Multithreading & Semáforos)**:
  - Verificação de vitória executada em paralelo por **27 threads simultâneas** (9 para linhas, 9 para colunas e 9 para sub-blocos 3×3) sincronizadas com `java.util.concurrent.Semaphore`.
- **Interface Gráfica Responsiva e Moderna**:
  - **Temas Claro (Dia) e Escuro (Noite)** alternáveis em tempo real tanto no menu inicial quanto durante a partida.
  - **Destaques Visuais Inteligentes**: iluminação automática da célula selecionada, de sua respectiva linha, coluna e quadrante 3×3, além de destaque para todas as células com o mesmo número.
  - **Modo Notas (Rascunho)**: permite anotar candidatos de `1` a `9` dentro das células vazias, limpando automaticamente notas conflitantes quando um valor definitivo é inserido.
  - **Feedback Visual de Conflitos**: animação de alerta e destaque em vermelho das células conflitantes sem interromper a partida com janelas modais.
  - **Sistema de Dicas, Reinício e Resolução Automática**: revele uma célula específica, limpe apenas as jogadas realizadas ou visualize a solução completa instantaneamente.
  - **Ícones Vetoriais (`SVGPath`)**: ícones renderizados nativamente em vetor para nitidez em qualquer resolução.

---

## Controles e Atalhos de Teclado

Além da interação completa via mouse e do teclado numérico lateral na tela, o jogo suporta navegação rápida pelo teclado:

| Tecla | Ação |
| :--- | :--- |
| **`1` – `9`** (ou Numpad) | Insere o número na célula selecionada (ou alterna candidato no Modo Notas) |
| **`Setas (↑ ↓ ← →)`** ou **`W A S D`** | Move a seleção pelo tabuleiro 9×9 |
| **`N`** | Ativa / desativa o **Modo Notas (Rascunho)** |
| **`Backspace`** / **`Delete`** / **`0`** | Apaga o valor ou as notas da célula selecionada |

---

## Arquitetura do Projeto

```text
Sudoku_game/
├── src/
│   ├── TabuleiroSudoku.java   # Regras de negócio, geração por backtracking e validação paralela (27 threads)
│   └── TelaSudoku.java        # Interface gráfica JavaFX, eventos de teclado/mouse, temas e ícones SVG
├── lib/                       # Dependências locais do JavaFX 17 (base, controls, graphics)
├── bin/                       # Bytecode compilado (.class)
├── iniciar.bat                # Script de compilação e execução rápida para Windows
├── pom.xml                    # Configuração de build e dependências via Apache Maven
└── LICENSE                    # Licença MIT
```

### Detalhamento dos Módulos

1. **`TabuleiroSudoku.java`**:
   - `gerarTabuleiroComDificuldade(String)`: preenche o tabuleiro completo via *backtracking* (`preencherTabuleiro`) e remove células verificando a unicidade da solução (`temUnicaSolucao` / `contarSolucoes`).
   - `validarJogada(int, int, int)`: verifica em $O(1)$ se um número respeita as regras de linha, coluna e sub-bloco 3×3.
   - `validarTabuleiroParalelo()`: instancia e coordena `ValidadorLinhaSudoku`, `ValidadorColunaSudoku` e `ValidadorBlocoSudoku` utilizando `Thread` e `Semaphore`.
2. **`TelaSudoku.java`**:
   - Gerencia o ciclo de vida da aplicação JavaFX mantendo uma única `Scene` (preservando o estado de maximização/tela cheia entre transições).
   - Renderiza o grid 9×9, cronômetro em tempo real (`Timeline`), contador de números restantes e painel de ações.

---

## Como Executar

### Pré-requisitos
- **Java Development Kit (JDK) 17** ou superior instalado e configurado no `PATH`.

### Opção 1: Execução Rápida no Windows (Recomendado)
O repositório já inclui as bibliotecas do JavaFX na pasta `lib/` e um script automatizado que compila o código-fonte em UTF-8 e inicia o jogo:

```powershell
.\iniciar.bat
```

### Opção 2: Via Linha de Comando (`javac` / `java`)
Para compilar e executar manualmente utilizando os módulos locais na pasta `lib/`:

```powershell
# 1. Compilar os arquivos-fonte
javac -encoding UTF-8 --module-path "lib" --add-modules javafx.controls -d bin src\TabuleiroSudoku.java src\TelaSudoku.java

# 2. Iniciar a aplicação
java --module-path "lib" --add-modules javafx.controls -cp bin TelaSudoku
```

### Opção 3: Via Apache Maven
Caso utilize o Maven, o `pom.xml` já está configurado com o `javafx-maven-plugin`:

```powershell
mvn clean javafx:run
```

---

## Licença

Distribuído sob a licença **MIT**. Consulte o arquivo [LICENSE](LICENSE) para mais informações.

---

**Autor:** Davi Castro
