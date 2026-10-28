package br.edu.ufersa.simplephysics.math.calculonumerico.metodosDiretos.decomposicaoLU;

// Algoritmo Decomposição LU (Algoritmo de Doolittle)
public class DecomposicaoLU {
  
// Método para decompor a matriz A em L e U com pivotamento parcial
public static ResultadoLU decomposicaoLU(double[][] A) {
    int n = A.length;
    double EPS = 1e-12;

    if (n == 0) {
        throw new IllegalArgumentException("A matriz nao pode ser vazia.");
    }

    for (int i = 0; i < n; i++) {
        if (A[i] == null || A[i].length != n) {
            throw new IllegalArgumentException(
                "A matriz deve ser quadrada."
            );
        }
    }

    double[][] U = new double[n][n];
    double[][] L = new double[n][n];
    int[] P = new int[n];

    for (int i = 0; i < n; i++) {
        U[i] = A[i].clone();
        L[i][i] = 1.0;
        P[i] = i;
    }

    for (int k = 0; k < n; k++) {

        // Busca o pivô na coluna k
        int linhaPivo = k;
        double maior = Math.abs(U[k][k]);

        for (int i = k + 1; i < n; i++) {
            if (Math.abs(U[i][k]) > maior) {
                maior = Math.abs(U[i][k]);
                linhaPivo = i;
            }
        }

        // Verifica se o pivô é suficientemente grande para evitar divisão por zero
        if (maior < EPS) {
            throw new ArithmeticException(
                "Matriz singular ou pivô muito proximo de zero."
            );
        }

        // Troca as linhas de U e registra a permutacao
        if (linhaPivo != k) {
            double[] tempU = U[k];
            U[k] = U[linhaPivo];
            U[linhaPivo] = tempU;

            int tempP = P[k];
            P[k] = P[linhaPivo];
            P[linhaPivo] = tempP;

            // Troca somente as colunas de L ja calculadas
            for (int j = 0; j < k; j++) {
                double tempL = L[k][j];
                L[k][j] = L[linhaPivo][j];
                L[linhaPivo][j] = tempL;
            }
        }

        // Calcula os multiplicadores e elimina os elementos abaixo do pivô.
        for (int i = k + 1; i < n; i++) {
            L[i][k] = U[i][k] / U[k][k];

            for (int j = k; j < n; j++) {
                U[i][j] -= L[i][k] * U[k][j];
            }
        }
    }

    return new ResultadoLU(L, U, P);
}


   
// Substituição progressiva para resolver Ly = Pb
public static double[] substituicaoProgressiva(
        double[][] L, double[] b, int[] P) {

    if (L == null || b == null || P == null
            || L.length != b.length
            || L.length != P.length) {
        throw new IllegalArgumentException(
            "As dimensoes de L, b e P devem ser compativeis."
        );
    }

    int n = L.length;
    double[] y = new double[n];

    for (int i = 0; i < n; i++) {
        if (L[i] == null || L[i].length != n) {
            throw new IllegalArgumentException(
                "A matriz L deve ser quadrada."
            );
        }

        double soma = 0.0;

        // Calcula a soma dos produtos L[i][j] * y[j] para j < i
        for (int j = 0; j < i; j++) {
            soma += L[i][j] * y[j];
        }

        if (Math.abs(L[i][i]) < 1e-12) {
            throw new ArithmeticException(
                "Elemento diagonal nulo em L."
            );
        }

        // Calcula y[i] usando a permutação P
        y[i] = (b[P[i]] - soma) / L[i][i];
    }

    return y;
}


// Substituição regressiva para resolver Ux = y
    public static double[] substituicaoRegressiva(
            double[][] U, double[] y) {

        int n = U.length;

        if (y == null || y.length != n) {
            throw new IllegalArgumentException(
                "O vetor y deve ter o mesmo tamanho da matriz."
            );
        }

        double[] x = new double[n];

        // Calcula x[i] a partir de y[i] e dos elementos de U
        for (int i = n - 1; i >= 0; i--) {
            double soma = 0.0;

            for (int k = i + 1; k < n; k++) {
                soma += U[i][k] * x[k];
            }

            if (Math.abs(U[i][i]) < 1e-12) {
                throw new ArithmeticException(
                    "Elemento diagonal nulo em U."
                );
            }

            // Calcula x[i] usando a substituição regressiva
            x[i] = (y[i] - soma) / U[i][i];
        }

        return x;
    }

    // Método para resolver o sistema Ax = b usando a decomposição LU
    public static double[] resolveSistemaLU(
            double[][] A, double[] b) {

        if (b == null || A == null || A.length != b.length) {
            throw new IllegalArgumentException(
                "As dimensoes de A e b sao incompatíveis."
            );
        }

        ResultadoLU resultado = decomposicaoLU(A);

        double[][] L = resultado.L;
        double[][] U = resultado.U;
        int[] P = resultado.P;

        double[] y = substituicaoProgressiva(L, b, P);

        return substituicaoRegressiva(U, y);
    }

    // Método para imprimir uma matriz
    public static void imprimirMatriz(String nome, double[][] M) {
        System.out.println(nome + ":");

        for (double[] linha : M) {
            for (double valor : linha) {
                System.out.printf("%10.4f ", valor);
            }

            System.out.println();
        }
    }

    // Método para imprimir um vetor
    public static void imprimirVetor(String nome, double[] v) {
        System.out.print(nome + ": [");

        for (int i = 0; i < v.length; i++) {
            System.out.printf("%.4f", v[i]);

            if (i < v.length - 1) {
                System.out.print(", ");
            }
        }

        System.out.println("]");
    }


    // Classe interna para armazenar o resultado da decomposição LU
    static class ResultadoLU {
        double [][] L;
        double [][] U;
        int [] P;

        ResultadoLU(double[][] L, double [][] U, int []P) {
            this.L = L;
            this.U = U;
            this.P = P;
        }

    }

    // ----------------------------------------------------------

    public static void main(String[] args) {
 
        double[][] A = {
        {0, 1},
        {1, 0}
        };

        double[] b = {2, 3};

        try {
            ResultadoLU resultado = decomposicaoLU(A);

            double[][] L = resultado.L;
            double[][] U = resultado.U;
            int[] P = resultado.P;

            double[] y = substituicaoProgressiva(L, b, P);
            double[] x = substituicaoRegressiva(U, y);


            System.out.println("Resultado da Decomposicao: ");
            imprimirMatriz("Matriz L", L);
            System.out.println("");
            imprimirMatriz("Matriz U", U);
            System.out.println("");
            imprimirVetor("Vetor b", b);
            imprimirVetor("Vetor y", y);
            imprimirVetor("Solucao x", x);
            System.out.println("");

            System.out.println("Permutacao P: " + java.util.Arrays.toString(P));

        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
