
package br.edu.ufersa.simplephysics.math.calculonumerico.metodosDiretos.eliminacaoGaussiana;

public class EliminacaoGaussiana {

    private static final double EPS = 1e-12;

    // Método de eliminação gaussiana com pivoteamento parcial
    public static double[] resolver(double[][] A, double[] b) {

        // Validar entradas
        validarEntradas(A, b);

        int n = A.length;
        double escalaA = escalaA(A);
        double tolerancia = EPS * escalaA;

        // Verificar se a matriz A possui apenas zeros
        if (escalaA == 0) {
            if (todosZero(b, EPS * Math.max(1.0, escalaB(b)))) {
                throw new ArithmeticException(
                    "O sistema possui infinitas solucoes."
                );
            }

            throw new ArithmeticException(
                "O sistema nao possui solucao."
            );
        }

        // Criar a matriz aumentada [A | b]
        double[][] M = new double[n][n + 1];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                M[i][j] = A[i][j];
            }

            M[i][n] = b[i];
        }

        // Eliminar os elementos abaixo dos pivôs
        int linhaPivo = 0;

        for (int k = 0; k < n; k++) {

            // Procurar o maior pivô em módulo na coluna atual
            int melhorLinha = linhaPivo;

            for (int i = linhaPivo + 1; i < n; i++) {
                if (Math.abs(M[i][k]) >
                    Math.abs(M[melhorLinha][k])) {
                    melhorLinha = i;
                }
            }

            // Se não houver pivô válido nesta coluna, passar para a próxima
            if (Math.abs(M[melhorLinha][k]) <= tolerancia) {
                continue;
            }

            // PIVOTEAMENTO PARCIAL: trocar linhas, se necessário
            if (melhorLinha != linhaPivo) {

                // AVISO: informa quando o pivoteamento é utilizado
                System.out.printf(
                    "Pivoteamento parcial utilizado na coluna %d: "
                    + "trocando as linhas %d e %d.%n",
                    k + 1, linhaPivo + 1, melhorLinha + 1
                );

                double[] temp = M[linhaPivo];
                M[linhaPivo] = M[melhorLinha];
                M[melhorLinha] = temp;
            }

            // Eliminar os elementos abaixo do pivô
            for (int i = linhaPivo + 1; i < n; i++) {

                double fator = M[i][k] / M[linhaPivo][k];

                M[i][k] = 0;

                for (int j = k + 1; j <= n; j++) {
                    M[i][j] -= fator * M[linhaPivo][j];
                }
            }

            linhaPivo++;
        }

        // Verificar se o sistema é inconsistente
        double toleranciaB = EPS * Math.max(1.0, escalaB(b));

        for (int i = 0; i < n; i++) {

            boolean linhaZero = true;

            for (int j = 0; j < n; j++) {
                if (Math.abs(M[i][j]) > tolerancia) {
                    linhaZero = false;
                    break;
                }
            }

            if (linhaZero && Math.abs(M[i][n]) > toleranciaB) {
                throw new ArithmeticException(
                    "O sistema nao possui solucao."
                );
            }
        }

        // Se faltarem pivôs, o sistema consistente possui infinitas soluções
        if (linhaPivo < n) {
            throw new ArithmeticException(
                "O sistema possui infinitas solucoes."
            );
        }

        // Resolver o sistema triangular por substituição retroativa
        double[] x = new double[n];

        for (int i = n - 1; i >= 0; i--) {

            double soma = M[i][n];

            for (int j = i + 1; j < n; j++) {
                soma -= M[i][j] * x[j];
            }

            x[i] = soma / M[i][i];
        }

        // Verificar se os resultados são números finitos
        for (double valor : x) {
            if (!Double.isFinite(valor)) {
                throw new ArithmeticException(
                    "O sistema produziu um resultado invalido."
                );
            }
        }

        return x;
    }

    // Método de eliminação gaussiana sem pivoteamento parcial
    public static double[] resolverSemPivoteamento(
        double[][] A, double[] b
    ) {

        // Validar entradas
        validarEntradas(A, b);

        int n = A.length;
        double escalaA = escalaA(A);
        double tolerancia = EPS * escalaA;
        double toleranciaB = EPS * Math.max(1.0, escalaB(b));

        if (escalaA == 0) {
            if (todosZero(b, toleranciaB)) {
                throw new ArithmeticException(
                    "O sistema possui infinitas solucoes."
                );
            }

            throw new ArithmeticException(
                "O sistema nao possui solucao."
            );
        }

        // Criar a matriz aumentada [A | b]
        double[][] M = new double[n][n + 1];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                M[i][j] = A[i][j];
            }

            M[i][n] = b[i];
        }

        // Eliminar os elementos abaixo da diagonal principal
        for (int k = 0; k < n; k++) {

            // Sem pivoteamento, o pivô será sempre o elemento diagonal
            double pivo = M[k][k];

            if (Math.abs(pivo) <= tolerancia) {
                throw new ArithmeticException(
                    "Pivo nulo ou muito proximo de zero na posicao "
                    + k + ". O metodo sem pivotamento nao pode "
                    + "continuar com este sistema."
                );
            }

            // Eliminar os elementos abaixo do pivô
            for (int i = k + 1; i < n; i++) {

                double fator = M[i][k] / pivo;

                M[i][k] = 0;

                for (int j = k + 1; j <= n; j++) {
                    M[i][j] -= fator * M[k][j];
                }
            }
        }

        // Verificar se o sistema é inconsistente
        for (int i = 0; i < n; i++) {

            boolean linhaZero = true;

            for (int j = 0; j < n; j++) {
                if (Math.abs(M[i][j]) > tolerancia) {
                    linhaZero = false;
                    break;
                }
            }

            if (linhaZero && Math.abs(M[i][n]) > toleranciaB) {
                throw new ArithmeticException(
                    "O sistema nao possui solucao."
                );
            }
        }

        // Resolver o sistema triangular por substituição retroativa
        double[] x = new double[n];

        for (int i = n - 1; i >= 0; i--) {

            double soma = M[i][n];

            for (int j = i + 1; j < n; j++) {
                soma -= M[i][j] * x[j];
            }

            x[i] = soma / M[i][i];

            if (!Double.isFinite(x[i])) {
                throw new ArithmeticException(
                    "O sistema produziu um resultado invalido."
                );
            }
        }

        return x;
    }

    // Validar as dimensões e os valores das entradas
    private static void validarEntradas(double[][] A, double[] b) {

        if (A == null || b == null) {
            throw new IllegalArgumentException(
                "A matriz A e o vetor b nao podem ser nulos."
            );
        }

        int n = A.length;

        if (n == 0 || b.length != n) {
            throw new IllegalArgumentException(
                "As dimensoes da matriz e do vetor sao invalidas."
            );
        }

        for (int i = 0; i < n; i++) {

            if (A[i] == null || A[i].length != n) {
                throw new IllegalArgumentException(
                    "A matriz A deve ser quadrada."
                );
            }

            for (int j = 0; j < n; j++) {
                if (!Double.isFinite(A[i][j])) {
                    throw new IllegalArgumentException(
                        "A matriz A contem valores invalidos."
                    );
                }
            }

            if (!Double.isFinite(b[i])) {
                throw new IllegalArgumentException(
                    "O vetor b contem valores invalidos."
                );
            }
        }
    }

    // Calcular o maior valor absoluto da matriz A
    private static double escalaA(double[][] A) {

        double escala = 0;

        for (double[] linha : A) {
            for (double valor : linha) {
                escala = Math.max(escala, Math.abs(valor));
            }
        }

        return escala;
    }

    // Calcular o maior valor absoluto do vetor b
    private static double escalaB(double[] b) {

        double escala = 0;

        for (double valor : b) {
            escala = Math.max(escala, Math.abs(valor));
        }

        return escala;
    }

    // Verificar se todos os elementos do vetor são próximos de zero
    private static boolean todosZero(double[] vetor, double tolerancia) {

        for (double valor : vetor) {
            if (Math.abs(valor) > tolerancia) {
                return false;
            }
        }

        return true;
    }

    // Imprimir uma matriz
    private static void imprimirMatriz(double[][] A) {

        for (double[] linha : A) {
            for (double valor : linha) {
                System.out.printf("%10.4f ", valor);
            }

            System.out.println();
        }
    }

    // Imprimir um vetor
    private static void imprimirVetor(double[] vetor) {

        System.out.print("[");

        for (int i = 0; i < vetor.length; i++) {

            System.out.printf("%.4f", vetor[i]);

            if (i < vetor.length - 1) {
                System.out.print(", ");
            }
        }

        System.out.println("]");
    }

    // Executar os testes dos dois métodos
    public static void main(String[] args) {

        double[][] A = {
            {0, 1},
            {1, 0}
        };

        double[] b = {2, 3};

        System.out.println("\n=== COM PIVOTEAMENTO PARCIAL ===");
        System.out.println("Matriz A original:");
        imprimirMatriz(A);

        System.out.print("Vetor b original: ");
        imprimirVetor(b);

        try {
            double[] solucao = resolver(A, b);

            System.out.print("Solucao x: ");
            imprimirVetor(solucao);

        } catch (ArithmeticException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }

        System.out.println();

        System.out.println("\n=== SEM PIVOTEAMENTO PARCIAL ===");

        try {
            double[] solucao = resolverSemPivoteamento(A, b);

            System.out.print("Solucao x: ");
            imprimirVetor(solucao);
            System.out.println();

        } catch (ArithmeticException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
            System.out.println();
        }
    }
}
