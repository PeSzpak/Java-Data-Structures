import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Gera um arquivo CSV com 100.000 valores numéricos reais aleatórios.
 * Execute: javac GenerateData.java && java GenerateData
 */
public class GenerateData {
    public static void main(String[] args) throws IOException {
        String filename = "dados.csv";
        int total = 100_000;
        Random rand = new Random(42);

        try (FileWriter fw = new FileWriter(filename)) {
            for (int i = 0; i < total; i++) {
                double value = rand.nextDouble() * 1_000_000; // 0 a 1.000.000
                fw.write(String.format("%.2f", value));
                if (i < total - 1) fw.write(",");
            }
        }
        System.out.println("Arquivo '" + filename + "' gerado com " + total + " valores.");
    }
}
