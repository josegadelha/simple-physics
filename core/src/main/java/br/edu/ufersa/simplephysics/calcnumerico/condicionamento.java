package br.edu.ufersa.simplephysics.calcnumerico;

import java.util.Locale;
import java.util.Scanner;

public class condicionamento {

    //Tolerância para barrar pivôs muito pequenos
    private static final double tolerancia = 1e-12;

    public static void main(String[] args) {

        try (Scanner sc = new Scanner(System.in)) {
            sc.useLocale(Locale.US);

            System.out.print("Ordem n da matriz quadrada A: ");
            int n = sc.nextInt();

            if (n < 1) {
                System.out.println("Erro: n deve ser maior ou igual a 1.");
                return;
            }

            double[][] A = new double[n][n];

            System.out.println(
                "Digite os elementos de A, linha por linha:"
            );

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    A[i][j] = sc.nextDouble();
                }
            }

            try {
                // Calcula a inversa.
                double[][] inversa = inverter(A);

                // Normas da matriz A.
                double norma1A = norma1(A);
                double normaInfA = normaInfinito(A);

                // Normas da inversa.
                double norma1Inv = norma1(inversa);
                double normaInfInv = normaInfinito(inversa);

                // Numeros de condicionamento.
                double kappa1 = norma1A * norma1Inv;
                double kappaInf = normaInfA * normaInfInv;

                System.out.println("\nMatriz A:");
                imprimirMatriz(A);

                System.out.println("\nMatriz inversa A^(-1):");
                imprimirMatriz(inversa);

                System.out.printf(
                    Locale.US, "%n||A||_1 = %.10f%n", norma1A
                );

                System.out.printf(
                    Locale.US, "||A^(-1)||_1 = %.10f%n", norma1Inv
                );

                System.out.printf(
                    Locale.US, "kappa_1(A) = %.10f%n", kappa1
                );

                System.out.printf(
                    Locale.US, "%n||A||_inf = %.10f%n", normaInfA
                );

                System.out.printf(
                    Locale.US,
                    "||A^(-1)||_inf = %.10f%n",
                    normaInfInv
                );

                System.out.printf(
                    Locale.US, "kappa_inf(A) = %.10f%n", kappaInf
                );

                System.out.printf(
                    Locale.US,
                    "%nPerda aproximada de digitos: %.2f%n",
                    Math.log10(kappaInf)
                );

            } catch (IllegalArgumentException e) {
                System.out.println(
                    "\nNao foi possivel calcular a inversa: "
                        + e.getMessage()
                );
            }

        } catch (java.util.InputMismatchException e) {
            System.out.println(
                "Entrada invalida. Digite numeros e use ponto nos decimais."
            );
        }
    }

    // Norma 1: maior soma dos modulos por coluna.
    private static double norma1(double[][] A) {

        int n = A.length;
        double maiorSoma = 0.0;

        for (int j = 0; j < n; j++) {
            double soma = 0.0;

            for (int i = 0; i < n; i++) {
                soma += Math.abs(A[i][j]);
            }

            maiorSoma = Math.max(maiorSoma, soma);
        }

        return maiorSoma;
    }

    // Norma infinito: maior soma dos modulos por linha.
    private static double normaInfinito(double[][] A) {

        int n = A.length;
        double maiorSoma = 0.0;

        for (int i = 0; i < n; i++) {
            double soma = 0.0;

            for (int j = 0; j < n; j++) {
                soma += Math.abs(A[i][j]);
            }

            maiorSoma = Math.max(maiorSoma, soma);
        }

        return maiorSoma;
    }

    // Calcula A^(-1) pelo metodo de Gauss-Jordan.
    // Utiliza pivoteamento parcial.
    private static double[][] inverter(double[][] A) {

        int n = A.length;
        double escala = 0.0;

        // Maior modulo entre os elementos da matriz.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                escala = Math.max(
                    escala, Math.abs(A[i][j])
                );
            }
        }

        if (escala == 0.0) {
            throw new IllegalArgumentException(
                "a matriz nula e singular."
            );
        }

        double tolerancia = condicionamento.tolerancia * escala;

        // Monta a matriz aumentada [A | I].
        double[][] aumentada = new double[n][2 * n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                aumentada[i][j] = A[i][j];
            }

            aumentada[i][n + i] = 1.0;
        }

        // Cada coluna fornecerá um pivô.
        for (int coluna = 0; coluna < n; coluna++) {

            int linhaPivo = coluna;

            // Escolhe o maior modulo na coluna.
            for (int i = coluna + 1; i < n; i++) {
                if (Math.abs(aumentada[i][coluna])
                    > Math.abs(aumentada[linhaPivo][coluna])) {

                    linhaPivo = i;
                }
            }

            // Um pivô pequeno demais pode indicar
            // singularidade ou quase singularidade.
            if (Math.abs(aumentada[linhaPivo][coluna])
                <= tolerancia) {

                throw new IllegalArgumentException(
                    "matriz singular ou numericamente quase singular."
                );
            }

            // Troca as linhas para colocar o pivô na posição correta.
            double[] temporaria = aumentada[coluna];
            aumentada[coluna] = aumentada[linhaPivo];
            aumentada[linhaPivo] = temporaria;

            // Divide a linha pelo pivô.
            double pivo = aumentada[coluna][coluna];

            for (int j = 0; j < 2 * n; j++) {
                aumentada[coluna][j] /= pivo;
            }

            // Zera os demais elementos da coluna do pivô.
            for (int i = 0; i < n; i++) {

                if (i != coluna) {
                    double fator = aumentada[i][coluna];

                    for (int j = 0; j < 2 * n; j++) {
                        aumentada[i][j] -=
                            fator * aumentada[coluna][j];
                    }
                }
            }
        }

        // Extrai A^(-1), que está à direita da matriz aumentada.
        double[][] inversa = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inversa[i][j] = aumentada[i][n + j];
            }
        }

        return inversa;
    }

    // Exibe uma matriz na tela.
    private static void imprimirMatriz(double[][] A) {

        for (double[] linha : A) {
            for (double valor : linha) {
                System.out.printf(Locale.US, "%14.6f", valor);
            }

            System.out.println();
        }
    }
}
