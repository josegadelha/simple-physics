package br.edu.ufersa.simplephysics.math.calculonumerico.interpolacao;

import java.util.Scanner;

public class Newton {

    public static double[] diferencasDivididas(double[] x, double[] y) {
        int quantidade = x.length;
        double[] coeficientes = y.clone();

        for (int ordem = 1; ordem < quantidade; ordem++) {
            for (int i = quantidade - 1; i >= ordem; i--) {
                coeficientes[i] =
                    (coeficientes[i] - coeficientes[i - 1])
                        / (x[i] - x[i - ordem]);
            }
        }

        return coeficientes;
    }

    public static double interpolar(
        double[] x, double[] coeficientes, double valor) {

        int quantidade = x.length;
        double resultado = coeficientes[quantidade - 1];

        for (int i = quantidade - 2; i >= 0; i--) {
            resultado = coeficientes[i] + (valor - x[i]) * resultado;
        }

        return resultado;
    }

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);

        System.out.print("Quantidade de pontos: ");
        int quantidade = entrada.nextInt();

        if (quantidade < 1) {
            System.out.println("Erro: a quantidade deve ser positiva.");
            entrada.close();
            return;
        }

        double[] x = new double[quantidade];
        double[] y = new double[quantidade];

        for (int i = 0; i < quantidade; i++) {
            System.out.printf("Digite x[%d]: ", i);
            x[i] = entrada.nextDouble();

            for (int j = 0; j < i; j++) {
                if (x[i] == x[j]) {
                    System.out.println("Erro: os valores de x devem ser distintos.");
                    entrada.close();
                    return;
                }
            }

            System.out.printf("Digite y[%d]: ", i);
            y[i] = entrada.nextDouble();
        }

        double[] coeficientes = diferencasDivididas(x, y);

        System.out.print("Valor de x para interpolar: ");
        double valor = entrada.nextDouble();

        double resultado = interpolar(x, coeficientes, valor);

        System.out.printf("Resultado: P(%.4f) = %.6f%n", valor, resultado);

        entrada.close();
    }
}
