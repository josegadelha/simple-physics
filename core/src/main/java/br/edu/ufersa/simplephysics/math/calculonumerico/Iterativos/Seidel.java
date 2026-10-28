package br.edu.ufersa.simplephysics.math.calculonumerico.Iterativos;


 public class Seidel {

        public static double[] executar(double[][] A, double[] b, double[] x0, double eps, int kmax) {
            int n = b.length;
            double[] x = x0.clone();

            for (int k = 1; k <= kmax; k++) {
                // Cópia do vetor da iteração anterior apenas para o cálculo do erro[cite: 11, 26]
                double[] xant = x.clone();

                // Cálculo dos novos valores de x (atualização no próprio lugar)[cite: 11, 23]
                for (int i = 0; i < n; i++) {
                    double soma = b[i];
                    for (int j = 0; j < n; j++) {
                        if (j != i) {
                            // Usa os valores mais recentes disponíveis na memória[cite: 8]
                            soma -= A[i][j] * x[j];
                        }
                    }
                    // Sobrescreve o valor antigo imediatamente[cite: 11]
                    x[i] = soma / A[i][i];
                }

                // Cálculo do erro relativo (norma do infinito)[cite: 10, 26]
                double maxDiferenca = 0.0;
                double maxNovoX = 0.0;

                for (int i = 0; i < n; i++) {
                    double diferenca = Math.abs(x[i] - xant[i]);
                    if (diferenca > maxDiferenca) {
                        maxDiferenca = diferenca;
                    }

                    double valorAbsoluto = Math.abs(x[i]);
                    if (valorAbsoluto > maxNovoX) {
                        maxNovoX = valorAbsoluto;
                    }
                }

                double dr = maxDiferenca / maxNovoX;

                // Critério de parada[cite: 11, 26]
                if (dr < eps) {
                    System.out.println("Gauss-Seidel convergiu em " + k + " iterações.");
                    return x;
                }
            }

            throw new RuntimeException("Gauss-Seidel não convergiu após " + kmax + " iterações[cite: 26].");
        }

    public static void main(String[] args) {

        double[][] A = {
            {10.0, 2.0, 1.0},
            {1.0, 5.0, 1.0},
            {2.0, 3.0, 10.0}
        };
        double[] b = {7.0, -8.0, 6.0};

        // Aproximação inicial com zeros e critérios de parada[cite: 15, 27]
        double[] x0 = {0.0, 0.0, 0.0};
        double eps = 0.001; // 10^-3
        int kmax = 100;

        System.out.println("Iniciando testes para convergencia proxima de (1, -2, 1)[cite: 15]\n");

        double[] resultadoSeidel = Seidel.executar(A, b, x0, eps, kmax);
        System.out.printf("Vetor final Seidel: [%.4f, %.4f, %.4f]\n",
            resultadoSeidel[0], resultadoSeidel[1], resultadoSeidel[2]);

    }
}
