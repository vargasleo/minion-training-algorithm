
public class Main {

    public static void main(String[] args) {

        //final var casos = new String[]{"src/main/oito_enunciado.txt", "src/main/quinhentos.txt", "src/main/mil.txt", "src/main/mil_e_quinhentos.txt", "src/main/tres_mil.txt"};
        String[] casos = {"src/main/caso0500.txt", "src/main/caso1000.txt", "src/main/caso3000.txt"};

        for (String caso : casos) {
            final var arquivo = new MinionTrainingAlgorithm();
            final var start = System.currentTimeMillis();
            arquivo.run(caso);
            final var end = System.currentTimeMillis();
            System.out.printf("Tempo execução: %.4f s%n", (end - start) / 1000d);
            System.out.println("*-.._--''¨¨--_..-*-.._--''¨¨--_..-*");
        }
    }
}
