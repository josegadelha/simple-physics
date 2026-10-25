package br.edu.ufersa.simplephysics.math.calculonumerico.metodosDiretos.eliminacaoGaussiana;


public class EliminacaoGaussiana {

    private static final double EPS = 1e-12;

    public static double[] resolver(double[][] A, double[] b) {

        // Validar entradas
        validarEntradas(A, b);

        int n = A.length;
        double escalaA = 0.0;

        // Calcular a escala da matriz A para determinar a tolerância
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                escalaA = Math.max(escalaA, Math.abs(A[i][j]));
            }
        }

        if (escalaA == 0.0) {
            if (todosZero(b, EPS)) {
                throw new ArithmeticException(
                    "Sistema com infinitas solucoes: "
                    + "todos os coeficientes sao zero."
                );
            }

            throw new ArithmeticException(
                "Sistema sem solucao: os coeficientes "
                + "sao zero, mas existe termo independente nao nulo."
            );
        }

        double tolerancia = EPS * escalaA;

        double[][] aug = new double[n][n + 1];

        // Criar a matriz aumentada [A | b].
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                aug[i][j] = A[i][j];
            }

            aug[i][n] = b[i];
        }

        // Eliminação Gaussiana com pivotamento parcial.
        int linhaPivo = 0;

        for (int coluna = 0; coluna < n; coluna++) {

            int p = linhaPivo;

            // Encontrar o pivô na coluna atual.
            for (int i = linhaPivo + 1; i < n; i++) {
                if (Math.abs(aug[i][coluna])
                        > Math.abs(aug[p][coluna])) {
                    p = i;
                }
            }

            // Se o pivô é muito pequeno, o sistema pode não ter solução única
            if (Math.abs(aug[p][coluna]) <= tolerancia) {
                continue;
            }

            // Trocar a linha do pivô com a linha atual
            if (p != linhaPivo) {
                double[] temp = aug[linhaPivo];
                aug[linhaPivo] = aug[p];
                aug[p] = temp;
            }

            // Eliminar as entradas abaixo do pivô na coluna atual
            for (int i = linhaPivo + 1; i < n; i++) {

                double m = aug[i][coluna] / aug[linhaPivo][coluna];

                aug[i][coluna] = 0.0;

                // Atualizar os elementos restantes da linha
                for (int k = coluna + 1; k <= n; k++) {
                    aug[i][k] -= m * aug[linhaPivo][k];
                }
            }

            linhaPivo++;
        }

        // Verificar se o sistema é consistente e se há solução única
        for (int i = 0; i < n; i++) {

            boolean coeficientesZero = true;

            // Verificar se todos os coeficientes da linha são zero
            for (int j = 0; j < n; j++) {
                if (Math.abs(aug[i][j]) > tolerancia) {
                    coeficientesZero = false;
                    break;
                }
            }

            // Se todos os coeficientes são zero, mas o termo independente não é, o sistema não tem solução
            if (coeficientesZero
                    && Math.abs(aug[i][n]) > EPS * escalaB(b)) {
                throw new ArithmeticException(
                    "Sistema sem solucao: foi encontrada "
                    + "uma linha contraditoria."
                );
            }
        }

        // Se a linha do pivô não alcançou o final, o sistema tem infinitas soluções
        if (linhaPivo < n) {
            throw new ArithmeticException(
                "Sistema com infinitas solucoes: "
                + "a matriz dos coeficientes nao tem posto completo."
            );
        }

        double[] x = new double[n];

        // Substituição regressiva para encontrar a solução
        for (int i = n - 1; i >= 0; i--) {

            double soma = 0.0;

            // Calcular a soma dos produtos das variáveis já resolvidas
            for (int j = i + 1; j < n; j++) {
                soma += aug[i][j] * x[j];
            }

            double pivo = aug[i][i];

            if (Math.abs(pivo) <= tolerancia) {
                throw new ArithmeticException(
                    "Pivo muito proximo de zero na substituicao."
                );
            }

            // Calcular o valor da variável atual
            x[i] = (aug[i][n] - soma) / pivo;
        }

        // Verificar se a solução contém valores NaN ou infinitos
        for (int i = 0; i < n; i++) {
            if (!Double.isFinite(x[i])) {
                throw new ArithmeticException(
                    "A solucao gerou um valor NaN ou infinito."
                );
            }
        }

        return x;
    }

    // Valida as entradas da matriz A e do vetor b
    private static void validarEntradas(double[][] A, double[] b) {

        // Verifica se A e b são nulos
        if (A == null || b == null) {
            throw new IllegalArgumentException(
                "A matriz A e o vetor b nao podem ser null."
            );
        }

        int n = A.length;

        // Verifica se A é uma matriz quadrada e se b tem o tamanho correto
        if (n == 0) {
            throw new IllegalArgumentException(
                "A matriz A nao pode ser vazia."
            );
        }

        // Verifica se b tem o mesmo número de elementos que A tem de linhas
        if (b.length != n) {
            throw new IllegalArgumentException(
                "O vetor b deve ter o mesmo numero de elementos "
                + "que A tem de linhas."
            );
        }

        // Verifica se cada linha de A tem o mesmo número de colunas que A tem de linhas
        for (int i = 0; i < n; i++) {

            if (A[i] == null || A[i].length != n) {
                throw new IllegalArgumentException(
                    "A matriz A deve ser quadrada."
                );
            }

            if (!Double.isFinite(b[i])) {
                throw new IllegalArgumentException(
                    "O vetor b nao pode conter NaN ou infinito."
                );
            }

            for (int j = 0; j < n; j++) {
                if (!Double.isFinite(A[i][j])) {
                    throw new IllegalArgumentException(
                        "A matriz A nao pode conter NaN ou infinito."
                    );
                }
            }
        }
    }

    // Calcula a maior magnitude do vetor b
    private static double escalaB(double[] b) {
        double escala = 0.0;

        // Calcula a maior magnitude do vetor b
        for (double valor : b) {
            escala = Math.max(escala, Math.abs(valor));
        }

        return escala;
    }

    // Verifica se todos os elementos do vetor b são zero dentro de uma tolerância
    private static boolean todosZero(double[] b, double tol) {
        
        for (double valor : b) {
            if (Math.abs(valor) > tol) {
                return false;
            }
        }

        return true;
    }

    // Exibe uma matriz
    public static void imprimirMatriz(String nome, double[][] M) {
        System.out.println(nome + ":");

        for (double[] linha : M) {
            for (double valor : linha) {
                System.out.printf("%10.4f ", valor);
            }

            System.out.println();
        }
    }

    // Exibe um vetor
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

    // ----------------------------------------------------------

    public static void main(String[] args) {

        double[][] A = {
            {0, 1},
            {1, 0}
        };

        double[] b = {2, 3};

        try {
            double[] x = resolver(A, b);

            System.out.println("Eliminacao Gaussiana");
            imprimirMatriz("Matriz A original", A);
            imprimirVetor("Vetor b original", b);
            imprimirVetor("Solucao x", x);

        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}