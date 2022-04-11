import org.apache.commons.lang3.SerializationUtils;

public class OptimizeTraining {
    private final MinionTrainingAlgorithm minionTrainingAlgorithm;

    public OptimizeTraining() {
        this.minionTrainingAlgorithm = new MinionTrainingAlgorithm();
    }

    public void findIdealMinionsPopulation(String filename) {
        Count.count++;
        final var start = System.currentTimeMillis();
        minionTrainingAlgorithm.readFile(filename);
        var minions = 0;
        var bestTime = Integer.MAX_VALUE;

        int actualTime;
        for (int j = 1; j < Integer.MAX_VALUE; j++) {
            Count.count++;
            final var treino = SerializationUtils.clone(minionTrainingAlgorithm);
            actualTime = treino.practice(j);
            if (actualTime < bestTime) {
                bestTime = actualTime;
                minions = j;
            } else {
                break;
            }
        }

        System.out.println(filename);
        System.out.println("minions  " + minions + ", " + "tempo  " + bestTime);
        System.out.printf("tempo de execução: %.4f s%n", (System.currentTimeMillis() - start) / 1000d);
        System.out.println("operacoes: " + Count.count);
        System.out.println("*-.._--''¨¨--_..-*-.._--''¨¨-");
    }
}
