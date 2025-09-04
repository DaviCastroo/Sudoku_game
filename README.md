Jogo de Sudoku em JavaFX 🎮

Um jogo de Sudoku com interface gráfica em JavaFX, intuitivo e funcional. Gere novos jogos, resolva automaticamente, limpe o tabuleiro e alterne temas! ✨

Estrutura do Repositório 📂

•
src/: Código fonte Java. 💻

•
TabuleiroSudoku.java: Lógica do jogo (geração, validação, solução). 🧠

•
TelaSudoku.java: Interface gráfica (GUI) e interação do usuário. 👀



•
bin/: Arquivos compilados. 📦

•
out/: Saída de produção. 📤

•
.idea/: Configurações IntelliJ IDEA. ⚙️

•
LICENSE: Licença MIT. 📜

Como o Código Funciona 💡

O jogo é dividido em lógica (TabuleiroSudoku.java) e interface (TelaSudoku.java).

TabuleiroSudoku.java 🎲

Gerencia o tabuleiro, gera novos jogos (com backtracking), valida movimentos e verifica soluções. Métodos principais incluem inicialização, definição/obtenção de valores, verificação de células fixas, impressão, embaralhamento e resolução. ✔️

TelaSudoku.java 🖥️

Controla a interface e interação. Exibe a tela inicial com opções de Novo Jogo, Carregar (futuro 💾), e Sair. Renderiza o tabuleiro, permite entrada de números, validações e exibe alertas. Inclui botões para Resolver, Limpar, Alternar Tema (claro/escuro 🌓) e Voltar ao Menu. 👆✅

Como Rodar o Jogo ▶️

Pré-requisitos ✅

•
Java Development Kit (JDK): Versão 11+ (com JavaFX). ☕

•
JavaFX SDK: Se não incluso no JDK, baixe em openjfx.io. 🌐

Passos para Executar 🚀

1.
Clone o Repositório:

2.
IDE (Recomendado): Importe para IntelliJ IDEA 💡 ou Eclipse 🌑. Configure o JavaFX SDK nas bibliotecas do projeto e execute TelaSudoku.main(). ▶️

3.
Linha de Comando (Avançado): Compile e execute, apontando para os módulos JavaFX. Exemplo:

Licença ⚖️

Este projeto está sob a Licença MIT. 📄




Autor: Davi Castro

