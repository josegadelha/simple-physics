package br.edu.ufersa.simplephysics.math.calculonumerico.interpolacao;

import java.util.Scanner;

public class Lagrange {

    public static double interpolar(double[] x, double[] y, double valor) {
        double resultado = 0.0;

        for (int i = 0; i < x.length; i++) {
            double termo = y[i];

            for (int j = 0; j < x.length; j++) {
                if (i != j) {
                    termo *= (valor - x[j]) / (x[i] - x[j]);
                }
            }

            resultado += termo;
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

        System.out.print("Valor de x para interpolar: ");
        double valor = entrada.nextDouble();

        double resultado = interpolar(x, y, valor);

        System.out.printf("Resultado: P(%.4f) = %.6f%n", valor, resultado);

        entrada.close();
    }
}
