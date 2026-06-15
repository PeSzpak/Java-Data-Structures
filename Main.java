import java.io.*;
import java.util.*;

/**
 * ADO 03 — Implementação dos 11 algoritmos de ordenação
 * Prof. MSc. Edgar Cabral
 *
 * Uso:
 *   javac Main.java SortResult.java SortAlgorithms.java GenerateData.java
 *   java GenerateData          (gera dados.csv com 100.000 valores)
 *   java Main dados.csv        (executa todos os algoritmos e exibe métricas)
 */
public class Main {

    private static final String[] NOMES = {
        "", "Bubble Sort", "Selection Sort", "Insertion Sort",
        "Merge Sort", "Quick Sort", "Heap Sort",
        "Counting Sort", "Radix Sort", "Bucket Sort",
        "Shell Sort", "Tim Sort"
    };

    // Algoritmos O(n²): rodam numa amostra pra não travar
    private static final int LIMITE_QUADRATICO = 10_000;

    public static void main(String[] args) throws IOException {
        String arquivo = (args.length > 0) ? args[0] : "dados.csv";

        cabecalho("ADO 03 — Algoritmos de Ordenação em Java");
        System.out.println("  Arquivo: " + arquivo);

        double[] dadosOriginais = lerCSV(arquivo);
        int n = dadosOriginais.length;
        System.out.printf("  Valores carregados: %,d%n%n", n);

        // ---- Executa todos os 11 algoritmos ----
        boolean usaAmostra = n > LIMITE_QUADRATICO;
        double[] amostra   = usaAmostra
            ? Arrays.copyOf(dadosOriginais, LIMITE_QUADRATICO)
            : dadosOriginais.clone();

        System.out.println("Executando algoritmos... aguarde.\n");

        SortResult[] resultados = new SortResult[12];

        // O(n²) — amostra
        resultados[1]  = SortAlgorithms.bubbleSort(amostra.clone());
        resultados[2]  = SortAlgorithms.selectionSort(amostra.clone());
        resultados[3]  = SortAlgorithms.insertionSort(amostra.clone());

        // O(n log n) / O(n+k) — base completa
        resultados[4]  = SortAlgorithms.mergeSort(dadosOriginais.clone());
        resultados[5]  = quickSortInstrumentado(dadosOriginais.clone());
        resultados[6]  = SortAlgorithms.heapSort(dadosOriginais.clone());
        resultados[7]  = SortAlgorithms.countingSort(dadosOriginais.clone());
        resultados[8]  = SortAlgorithms.radixSort(dadosOriginais.clone());
        resultados[9]  = SortAlgorithms.bucketSort(dadosOriginais.clone());
        resultados[10] = SortAlgorithms.shellSort(dadosOriginais.clone());
        resultados[11] = SortAlgorithms.timSort(dadosOriginais.clone());

        // ---- Exibe tabela de métricas ----
        exibirTabela(resultados, usaAmostra);

        // ---- Menu do usuário ----
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nSelecione um algoritmo para gerar o arquivo ordenado:");
        for (int i = 1; i <= 11; i++)
            System.out.printf("  [%2d] %s%n", i, NOMES[i]);
        System.out.print("Opção (1-11): ");

        int escolha = -1;
        try { escolha = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException ignored) {}

        if (escolha < 1 || escolha > 11) {
            System.out.println("Opção inválida. Encerrando.");
            return;
        }

        System.out.printf("%nOrdenando %,d elementos com %s...%n", n, NOMES[escolha]);
        double[] resultado = dadosOriginais.clone();
        SortResult r = ordenarComAlgoritmo(resultado, escolha);

        String saida = "ordenado_" + NOMES[escolha].replace(" ", "_").toLowerCase() + ".csv";
        gravarCSV(resultado, saida);

        System.out.println("Arquivo gerado: " + saida);
        System.out.printf("Tempo: %d ms | Comparações: %,d | Trocas: %,d | Passos: %,d%n",
            r.tempoMs, r.comparacoes, r.trocas, r.passos);
    }

    // ---- Exibição -------------------------------------------------------

    private static void exibirTabela(SortResult[] res, boolean usaAmostra) {
        String linha = "=".repeat(105);
        String sep   = "-".repeat(105);

        System.out.println(linha);
        System.out.printf("%-4s %-18s %9s %20s %20s %22s  %s%n",
            "Nº", "Algoritmo", "Tempo(ms)",
            "Comparações", "Trocas", "Passos totais", "Nota");
        System.out.println(sep);

        for (int i = 1; i <= 11; i++) {
            SortResult r = res[i];
            String nota = (i <= 3 && usaAmostra)
                ? String.format("[amostra %,d elem.]", LIMITE_QUADRATICO)
                : String.format("[%,d elem.]", r.tamanho);
            System.out.printf("%-4d %-18s %9d %,20d %,20d %,22d  %s%n",
                i, r.nome, r.tempoMs, r.comparacoes, r.trocas, r.passos, nota);
        }

        System.out.println("=".repeat(105));
        System.out.println();

        // Ranking por tempo (base completa apenas)
        System.out.println("  Ranking por tempo (algoritmos rodados na base completa):");
        List<SortResult> ranking = new ArrayList<>();
        for (int i = 4; i <= 11; i++) ranking.add(res[i]);
        ranking.sort(Comparator.comparingLong(r -> r.tempoMs));
        for (int i = 0; i < ranking.size(); i++)
            System.out.printf("    %dº %s — %d ms%n", i + 1,
                ranking.get(i).nome, ranking.get(i).tempoMs);
        System.out.println();
    }

    private static void cabecalho(String titulo) {
        int w = 60;
        System.out.println("=".repeat(w));
        System.out.printf("  %s%n", titulo);
        System.out.println("=".repeat(w));
    }

    // ---- Quick Sort iterativo com contadores ----------------------------

    private static SortResult quickSortInstrumentado(double[] arr) {
        long comp = 0, trocas = 0;
        long inicio = System.currentTimeMillis();

        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{0, arr.length - 1});

        while (!stack.isEmpty()) {
            int[] range = stack.pop();
            int low = range[0], high = range[1];
            if (low < high) {
                // mediana de três
                int mid = low + (high - low) / 2;
                if (arr[low] > arr[mid])  { double t=arr[low]; arr[low]=arr[mid];  arr[mid]=t;  trocas++; } comp++;
                if (arr[low] > arr[high]) { double t=arr[low]; arr[low]=arr[high]; arr[high]=t; trocas++; } comp++;
                if (arr[mid] > arr[high]) { double t=arr[mid]; arr[mid]=arr[high]; arr[high]=t; trocas++; } comp++;
                // coloca mediana como pivô
                double t = arr[mid]; arr[mid] = arr[high]; arr[high] = t; trocas++;

                double pivot = arr[high];
                int i = low - 1;
                for (int j = low; j < high; j++) {
                    comp++;
                    if (arr[j] <= pivot) {
                        i++;
                        double tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
                        trocas++;
                    }
                }
                double tmp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = tmp;
                trocas++;
                int pi = i + 1;
                stack.push(new int[]{low, pi - 1});
                stack.push(new int[]{pi + 1, high});
            }
        }

        return new SortResult("Quick Sort",
            System.currentTimeMillis() - inicio, comp, trocas, arr.length);
    }

    // ---- Utilitários ----------------------------------------------------

    private static double[] lerCSV(String path) throws IOException {
        List<Double> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                for (String token : linha.split("[,;\\s]+")) {
                    token = token.trim();
                    if (!token.isEmpty()) {
                        try { lista.add(Double.parseDouble(token.replace(",", "."))); }
                        catch (NumberFormatException ignored) {}
                    }
                }
            }
        }
        double[] arr = new double[lista.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = lista.get(i);
        return arr;
    }

    private static void gravarCSV(double[] arr, String path) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (int i = 0; i < arr.length; i++) {
                bw.write(String.format("%.2f", arr[i]));
                if (i < arr.length - 1) bw.write(",");
            }
        }
    }

    private static SortResult ordenarComAlgoritmo(double[] arr, int opcao) {
        return switch (opcao) {
            case 1  -> SortAlgorithms.bubbleSort(arr);
            case 2  -> SortAlgorithms.selectionSort(arr);
            case 3  -> SortAlgorithms.insertionSort(arr);
            case 4  -> SortAlgorithms.mergeSort(arr);
            case 5  -> quickSortInstrumentado(arr);
            case 6  -> SortAlgorithms.heapSort(arr);
            case 7  -> SortAlgorithms.countingSort(arr);
            case 8  -> SortAlgorithms.radixSort(arr);
            case 9  -> SortAlgorithms.bucketSort(arr);
            case 10 -> SortAlgorithms.shellSort(arr);
            case 11 -> SortAlgorithms.timSort(arr);
            default -> throw new IllegalArgumentException("Opção inválida");
        };
    }
}
