/**
 * Implementação dos 11 algoritmos de ordenação.
 * Cada método recebe um double[] e retorna um SortResult com:
 *   - tempo em ms
 *   - número de comparações
 *   - número de trocas (movimentações de elemento)
 */
public class SortAlgorithms {

    // =========================================================
    // 1. BUBBLE SORT  O(n²)
    // =========================================================
    public static SortResult bubbleSort(double[] arr) {
        long comp = 0, trocas = 0;
        long inicio = System.currentTimeMillis();

        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                comp++;
                if (arr[j] > arr[j + 1]) {
                    double tmp = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = tmp;
                    trocas++;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }

        return new SortResult("Bubble Sort",
            System.currentTimeMillis() - inicio, comp, trocas, arr.length);
    }

    // =========================================================
    // 2. SELECTION SORT  O(n²)
    // =========================================================
    public static SortResult selectionSort(double[] arr) {
        long comp = 0, trocas = 0;
        long inicio = System.currentTimeMillis();

        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                comp++;
                if (arr[j] < arr[minIdx]) minIdx = j;
            }
            if (minIdx != i) {
                double tmp = arr[minIdx]; arr[minIdx] = arr[i]; arr[i] = tmp;
                trocas++;
            }
        }

        return new SortResult("Selection Sort",
            System.currentTimeMillis() - inicio, comp, trocas, arr.length);
    }

    // =========================================================
    // 3. INSERTION SORT  O(n²)
    // =========================================================
    public static SortResult insertionSort(double[] arr) {
        long comp = 0, trocas = 0;
        long inicio = System.currentTimeMillis();

        int n = arr.length;
        for (int i = 1; i < n; i++) {
            double key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                comp++;
                arr[j + 1] = arr[j]; // desloca elemento (conta como troca)
                trocas++;
                j--;
            }
            if (j >= 0) comp++; // comparação que falhou no while
            arr[j + 1] = key;
        }

        return new SortResult("Insertion Sort",
            System.currentTimeMillis() - inicio, comp, trocas, arr.length);
    }

    // =========================================================
    // 4. MERGE SORT  O(n log n)
    // =========================================================
    public static SortResult mergeSort(double[] arr) {
        long[] contadores = {0, 0}; // [0]=comp, [1]=trocas
        long inicio = System.currentTimeMillis();

        mergeSortRec(arr, 0, arr.length - 1, contadores);

        return new SortResult("Merge Sort",
            System.currentTimeMillis() - inicio, contadores[0], contadores[1], arr.length);
    }

    private static void mergeSortRec(double[] arr, int left, int right, long[] c) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSortRec(arr, left, mid, c);
            mergeSortRec(arr, mid + 1, right, c);
            merge(arr, left, mid, right, c);
        }
    }

    private static void merge(double[] arr, int left, int mid, int right, long[] c) {
        int n1 = mid - left + 1, n2 = right - mid;
        double[] L = new double[n1], R = new double[n2];
        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);
        // cópias iniciais contam como trocas
        c[1] += n1 + n2;

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            c[0]++; // comparação
            if (L[i] <= R[j]) arr[k++] = L[i++];
            else               arr[k++] = R[j++];
            c[1]++; // escrita no array
        }
        while (i < n1) { arr[k++] = L[i++]; c[1]++; }
        while (j < n2) { arr[k++] = R[j++]; c[1]++; }
    }

    // versão sem contadores (usada internamente pelo Tim Sort)
    static void mergeSimples(double[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1, n2 = right - mid;
        double[] L = new double[n1], R = new double[n2];
        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2)
            arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // =========================================================
    // 5. QUICK SORT  O(n log n) médio
    // =========================================================
    public static SortResult quickSort(double[] arr) {
        long[] contadores = {0, 0};
        long inicio = System.currentTimeMillis();

        quickSortRec(arr, 0, arr.length - 1, contadores);

        return new SortResult("Quick Sort",
            System.currentTimeMillis() - inicio, contadores[0], contadores[1], arr.length);
    }

    private static void quickSortRec(double[] arr, int low, int high, long[] c) {
        if (low < high) {
            int pi = partition(arr, low, high, c);
            quickSortRec(arr, low, pi - 1, c);
            quickSortRec(arr, pi + 1, high, c);
        }
    }

    private static int partition(double[] arr, int low, int high, long[] c) {
        double pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            c[0]++; // comparação com pivô
            if (arr[j] <= pivot) {
                i++;
                double tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
                c[1]++;
            }
        }
        double tmp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = tmp;
        c[1]++;
        return i + 1;
    }

    // =========================================================
    // 6. HEAP SORT  O(n log n)
    // =========================================================
    public static SortResult heapSort(double[] arr) {
        long[] contadores = {0, 0};
        long inicio = System.currentTimeMillis();

        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(arr, n, i, contadores);
        for (int i = n - 1; i > 0; i--) {
            double tmp = arr[0]; arr[0] = arr[i]; arr[i] = tmp;
            contadores[1]++;
            heapify(arr, i, 0, contadores);
        }

        return new SortResult("Heap Sort",
            System.currentTimeMillis() - inicio, contadores[0], contadores[1], arr.length);
    }

    private static void heapify(double[] arr, int n, int i, long[] c) {
        int largest = i, l = 2 * i + 1, r = 2 * i + 2;
        if (l < n) { c[0]++; if (arr[l] > arr[largest]) largest = l; }
        if (r < n) { c[0]++; if (arr[r] > arr[largest]) largest = r; }
        if (largest != i) {
            double tmp = arr[i]; arr[i] = arr[largest]; arr[largest] = tmp;
            c[1]++;
            heapify(arr, n, largest, c);
        }
    }

    // =========================================================
    // 7. COUNTING SORT  O(n + k)
    // =========================================================
    public static SortResult countingSort(double[] arr) {
        long comp = 0, trocas = 0;
        long inicio = System.currentTimeMillis();

        long min = (long) arr[0], max = (long) arr[0];
        for (double v : arr) {
            comp += 2; // duas comparações por elemento (min e max)
            if ((long) v < min) min = (long) v;
            if ((long) v > max) max = (long) v;
        }

        long range = max - min + 1;
        long[] count = new long[(int) range];

        for (double v : arr) {
            count[(int)((long) v - min)]++;
            trocas++; // escrita no array de contagem
        }

        int idx = 0;
        for (int i = 0; i < range; i++) {
            comp++; // verifica count[i]
            while (count[i]-- > 0) {
                arr[idx++] = i + min;
                trocas++; // escrita no array resultado
            }
        }

        return new SortResult("Counting Sort",
            System.currentTimeMillis() - inicio, comp, trocas, arr.length);
    }

    // =========================================================
    // 8. RADIX SORT  O(d * n)
    // =========================================================
    public static SortResult radixSort(double[] arr) {
        long[] contadores = {0, 0};
        long inicio = System.currentTimeMillis();

        long min = (long) arr[0];
        for (double v : arr) {
            contadores[0]++;
            if ((long) v < min) min = (long) v;
        }

        long[] pos = new long[arr.length];
        for (int i = 0; i < arr.length; i++) {
            pos[i] = (long) arr[i] - min;
            contadores[1]++;
        }

        long max = pos[0];
        for (long v : pos) { contadores[0]++; if (v > max) max = v; }

        for (long exp = 1; max / exp > 0; exp *= 10)
            countingByDigit(pos, exp, contadores);

        for (int i = 0; i < arr.length; i++) {
            arr[i] = pos[i] + min;
            contadores[1]++;
        }

        return new SortResult("Radix Sort",
            System.currentTimeMillis() - inicio, contadores[0], contadores[1], arr.length);
    }

    private static void countingByDigit(long[] arr, long exp, long[] c) {
        int n = arr.length;
        long[] output = new long[n];
        int[] count = new int[10];

        for (long v : arr) { count[(int)((v / exp) % 10)]++; c[1]++; }
        for (int i = 1; i < 10; i++) count[i] += count[i - 1];
        for (int i = n - 1; i >= 0; i--) {
            int digit = (int)((arr[i] / exp) % 10);
            output[--count[digit]] = arr[i];
            c[1]++;
        }
        System.arraycopy(output, 0, arr, 0, n);
        c[1] += n;
    }

    // =========================================================
    // 9. BUCKET SORT  O(n + k) médio
    // =========================================================
    public static SortResult bucketSort(double[] arr) {
        long comp = 0, trocas = 0;
        long inicio = System.currentTimeMillis();

        int n = arr.length;
        double min = arr[0], max = arr[0];
        for (double v : arr) {
            comp += 2;
            if (v < min) min = v;
            if (v > max) max = v;
        }

        double range = max - min + 1e-9;
        int bucketCount = (int) Math.sqrt(n);

        @SuppressWarnings("unchecked")
        java.util.ArrayList<Double>[] buckets = new java.util.ArrayList[bucketCount];
        for (int i = 0; i < bucketCount; i++)
            buckets[i] = new java.util.ArrayList<>();

        for (double v : arr) {
            int idx = (int) ((v - min) / range * bucketCount);
            if (idx >= bucketCount) idx = bucketCount - 1;
            buckets[idx].add(v);
            trocas++; // inserção no balde
        }

        int pos = 0;
        for (java.util.ArrayList<Double> bucket : buckets) {
            // estimativa de comparações do sort interno (n log n por balde)
            int sz = bucket.size();
            if (sz > 1) comp += (long)(sz * Math.log(sz) / Math.log(2));
            java.util.Collections.sort(bucket);
            for (double v : bucket) { arr[pos++] = v; trocas++; }
        }

        return new SortResult("Bucket Sort",
            System.currentTimeMillis() - inicio, comp, trocas, arr.length);
    }

    // =========================================================
    // 10. SHELL SORT  O(n log² n)
    // =========================================================
    public static SortResult shellSort(double[] arr) {
        long comp = 0, trocas = 0;
        long inicio = System.currentTimeMillis();

        int n = arr.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                double temp = arr[i];
                int j = i;
                while (j >= gap) {
                    comp++;
                    if (arr[j - gap] > temp) {
                        arr[j] = arr[j - gap];
                        trocas++;
                        j -= gap;
                    } else break;
                }
                arr[j] = temp;
            }
        }

        return new SortResult("Shell Sort",
            System.currentTimeMillis() - inicio, comp, trocas, arr.length);
    }

    // =========================================================
    // 11. TIM SORT  O(n log n)
    // =========================================================
    private static final int RUN = 32;

    public static SortResult timSort(double[] arr) {
        long comp = 0, trocas = 0;
        long inicio = System.currentTimeMillis();

        int n = arr.length;

        // Fase 1: Insertion Sort em blocos de RUN
        for (int i = 0; i < n; i += RUN) {
            int end = Math.min(i + RUN - 1, n - 1);
            long[] c = insertionSortRange(arr, i, end);
            comp   += c[0];
            trocas += c[1];
        }

        // Fase 2: Merge dos blocos
        for (int size = RUN; size < n; size = 2 * size) {
            for (int left = 0; left < n; left += 2 * size) {
                int mid   = Math.min(left + size - 1, n - 1);
                int right = Math.min(left + 2 * size - 1, n - 1);
                if (mid < right) {
                    int bloco = right - left + 1;
                    comp   += (long)(bloco * Math.log(bloco) / Math.log(2));
                    trocas += bloco;
                    mergeSimples(arr, left, mid, right);
                }
            }
        }

        return new SortResult("Tim Sort",
            System.currentTimeMillis() - inicio, comp, trocas, arr.length);
    }

    private static long[] insertionSortRange(double[] arr, int left, int right) {
        long comp = 0, trocas = 0;
        for (int i = left + 1; i <= right; i++) {
            double key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                comp++;
                arr[j + 1] = arr[j];
                trocas++;
                j--;
            }
            if (j >= left) comp++;
            arr[j + 1] = key;
        }
        return new long[]{comp, trocas};
    }
}
