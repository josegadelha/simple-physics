package br.edu.ufersa.simplephysics.math.calculonumerico.Iterativos;

public class Jacobi {

    public static double[] executar(double[][] A, double[] b, double[] x0, double eps, int kmax) {
        int n = b.length;
        double[] x = x0.clone();

        for (int k = 1; k <= kmax; k++) {
            double[] xn = new double[n]; // Vetor temporário para a nova iteração[cite: 11, 26]

            // Cálculo dos novos valores de x
            for (int i = 0; i < n; i++) {
                double soma = b[i];
                for (int j = 0; j < n; j++) {
                    if (j != i) {
                        soma -= A[i][j] * x[j];
                    }
                }
                xn[i] = soma / A[i][i];
            }

            // Cálculo do erro relativo (norma do infinito)[cite: 10, 26]
            double maxDiferenca = 0.0;
            double maxNovoX = 0.0;

            for (int i = 0; i < n; i++) {
                double diferenca = Math.abs(xn[i] - x[i]);
                if (diferenca > maxDiferenca) {
                    maxDiferenca = diferenca;
                }

                double valorAbsoluto = Math.abs(xn[i]);
                if (valorAbsoluto > maxNovoX) {
                    maxNovoX = valorAbsoluto;
                }
            }

            double dr = maxDiferenca / maxNovoX;

            // Atualiza o vetor antigo com os novos valores[cite: 11, 26]
            x = xn.clone();

            // Critério de parada[cite: 11, 26]
            if (dr < eps) {
                System.out.println("Gauss-Jacobi convergiu em " + k + " iterações.");
                return x;
            }
        }

        throw new RuntimeException("Gauss-Jacobi não convergiu após " + kmax + " iterações.");
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

        double[] resultadoJacobi = Jacobi.executar(A, b, x0, eps, kmax);
        System.out.printf("Vetor final Jacobi: [%.4f, %.4f, %.4f]\n\n",
            resultadoJacobi[0], resultadoJacobi[1], resultadoJacobi[2]);

    }

}
