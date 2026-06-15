/**
 * Guarda as métricas de execução de um algoritmo de ordenação.
 */
public class SortResult {
    public final String nome;
    public final long   tempoMs;       // tempo em milissegundos
    public final long   comparacoes;   // quantas vezes dois elementos foram comparados
    public final long   trocas;        // quantas vezes dois elementos mudaram de posição
    public final long   passos;        // comparações + trocas (total de operações)
    public final int    tamanho;       // tamanho do array processado

    public SortResult(String nome, long tempoMs, long comparacoes, long trocas, int tamanho) {
        this.nome        = nome;
        this.tempoMs     = tempoMs;
        this.comparacoes = comparacoes;
        this.trocas      = trocas;
        this.passos      = comparacoes + trocas;
        this.tamanho     = tamanho;
    }

    @Override
    public String toString() {
        return String.format("%-18s | %6d ms | %,15d comp. | %,15d trocas | %,18d passos",
            nome, tempoMs, comparacoes, trocas, passos);
    }
}
