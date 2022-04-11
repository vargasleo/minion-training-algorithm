
public class Main {

    public static void main(String[] args) {

        final var files = new String[]{
                "oito_enunciado.txt",
                "dez.txt",
                "trinta.txt",
                "cinquenta.txt",
                "cem.txt",
                "quinhentos.txt",
                "mil.txt",
                "mil_e_quinhentos.txt",
                "dois_mil.txt",
                "tres_mil.txt",
                "quatro_mil.txt",
                "cinco_mil.txt"};

        for (String file : files) {
            Count.count = 0;
            final var optimizeTraining = new OptimizeTraining();
            optimizeTraining.findIdealMinionsPopulation(file);
        }
    }
}
