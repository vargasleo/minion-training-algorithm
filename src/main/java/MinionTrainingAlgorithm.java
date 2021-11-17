import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

public class MinionTrainingAlgorithm {

    private static class Vertex implements Comparable<Vertex> {
        private final String label;
        private int time;
        private int dependencies;

        public Vertex(String label, int time, int dependencies) {
            this.label = label;
            this.time = time;
            this.dependencies = dependencies;
        }

        public String getLabel() {
            return label;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public int getDependencies() {
            return dependencies;
        }

        public void setDependencies(int dependencies) {
            this.dependencies = dependencies;
        }

        @Override
        public int compareTo(Vertex o) {
            return this.getLabel().compareTo(o.getLabel());
        }
    }

    private final List<Vertex> root;
    private final Map<Vertex, List<Vertex>> graph;

    public MinionTrainingAlgorithm() {
        root = new ArrayList<>();
        graph = new HashMap<>();
    }

    public void run(String filename) {
        final var filepath = Paths.get(filename);
        try (BufferedReader reader = Files.newBufferedReader(filepath, Charset.defaultCharset())) {
            String line;
            while ((line = reader.readLine()) != null && !line.startsWith("}")) {
                final var word = line.split(" -> ");
                final var a = word[0].split("_");
                final var b = word[1].split("_");
                this.addObstacle(a[0], Integer.parseInt(a[1]), 0);
                this.addObstacle(b[0], Integer.parseInt(b[1]), 0);
                this.addDependencie(b[0]);
                this.addEdge(a[0], b[0]);
            }
            this.evaluateTraining(filename);
        } catch (IOException e) {
            System.err.println("cannot read file " + e);
        }
    }

    private int practice(List<Vertex> obstacles, Map<Vertex, List<Vertex>> graph, int workers) {
        /**
         * cria-se uma raiz e a vincula à todos os obstáculos sem pendências
         */
        final var root = new Vertex("root", 0, 0);

        obstacles.stream()
                .filter(o -> o.getDependencies() == 0)
                .forEach(o -> {
                    graph.computeIfAbsent(root, k -> new ArrayList<>());
                    graph.get(root).add(o);
                    o.setDependencies(o.getDependencies() + 1);
                });

        List<Vertex> working = new ArrayList<>();
        working.add(root);

        List<Vertex> avaliable = new ArrayList<>();

        int time = 0;
        /**
         *  enquanto houverem obstáculos disponíveis ou sendo executados, executa percurso
         */
        while (!avaliable.isEmpty() || !working.isEmpty()) {
            /**
             *  remove-se o menor obstáculo e incrementa o tempo do percurso
             */

            working.sort(Comparator.comparing(Vertex::getLabel));

            final var shortestObstacle = working.stream()
                    .min(Comparator.comparing(Vertex::getTime))
                    .orElseThrow(NoSuchElementException::new);

            working.remove(shortestObstacle);
            time += shortestObstacle.getTime();

            /**
             *  decrementa-se as dependências dos obstáculos a quem o obstáculo atual pertence,
             *  e os obstáculos sem dependências são adicionados na lista de disponíveis
             */

            AtomicBoolean flag = new AtomicBoolean(true);

            working.forEach(i -> {
                i.setTime(i.getTime() - shortestObstacle.getTime());
                if (i.getTime() <= 0)
                    flag.set(false);
            });

            if (graph.get(shortestObstacle) != null) {
                obstacles.stream()
                        .filter(i -> graph.get(shortestObstacle).contains(i))
                        .peek(i -> i.setDependencies(i.getDependencies() - 1))
                        .filter(i -> i.getDependencies() == 0)
                        .forEach(avaliable::add);
            }

            /**
             *  ordena em ordem alfabética
             */
            avaliable.sort(Comparator.comparing(Vertex::getLabel));

            /**
             * enquanto houverem obstáculos disponíveis E o número de obstáculos sendo executados for
             * menor que o número de trabalhadores E o tempo não tiver acabado ainda,
             * move o primeiro obstáculo disponível para a lista de obstáculos sendo superados
             */
            while (working.size() < workers && !avaliable.isEmpty() && flag.getAcquire()) {
                final var firstAvaliable = avaliable.get(0);
                working.add(firstAvaliable);
                avaliable.remove(firstAvaliable);
            }
        }
        return time;
    }

    public void addObstacle(String label, int time, int dependencies) {
        final var notPresent = root
                .stream()
                .noneMatch(i -> i.getLabel().equals(label));

        if (notPresent) {
            root.add(new Vertex(label, time, dependencies));
        }
    }

    public void addDependencie(String label) {
        root.stream()
                .filter(i -> i.getLabel().equals(label))
                .findFirst()
                .ifPresent(i -> i.setDependencies(i.getDependencies() + 1));
    }

    public void addEdge(String labelA, String labelB) {
        Supplier<RuntimeException> notPresentException = () -> new RuntimeException("vertex not present");

        Function<String, Vertex> findVertex = i -> root.stream()
                .filter(j -> j.getLabel().equals(i))
                .findFirst()
                .orElseThrow(notPresentException);

        final var a = findVertex.apply(labelA);
        final var b = findVertex.apply(labelB);

        graph.computeIfAbsent(a, k -> new ArrayList<>());
        graph.get(a).add(b);
    }

    private void copy(List<Vertex> obstaculosCopy, Map<Vertex, List<Vertex>> grafoCopy) {
        root.forEach(i -> obstaculosCopy.add(new Vertex(i.getLabel(), i.getTime(), i.getDependencies())));

        Set<Vertex> chaves = graph.keySet();

        for (Vertex chave : chaves) {
            Vertex obstaculoA = new Vertex(null, 0, 0);
            for (Vertex a : obstaculosCopy) {
                if (a.getLabel().equals(chave.getLabel())) {
                    obstaculoA = a;
                    break;
                }
            }

            List<Vertex> valoresKey = graph.get(chave);
            for (Vertex v : valoresKey) {
                Vertex obstaculoB = new Vertex(null, 0, 0);
                for (Vertex b : obstaculosCopy) {
                    if (b.getLabel().equals(v.getLabel())) {
                        obstaculoB = b;
                        break;
                    }
                }
                if (grafoCopy.get(obstaculoA) == null) {
                    grafoCopy.put(obstaculoA, new ArrayList<Vertex>());
                    grafoCopy.get(obstaculoA).add(obstaculoB);
                } else {
                    grafoCopy.get(obstaculoA).add(obstaculoB);
                }
            }
        }
    }

    public void evaluateTraining(String caso) {
        int t = 0;
        int auxM = 0;
        int auxT = 10000000;

        for (int j = 1; j < 101; j++) {

            List<Vertex> obstaculosCopy = new ArrayList<>();
            Map<Vertex, List<Vertex>> grafoCopy = new HashMap<>();

            copy(obstaculosCopy, grafoCopy);

            t = practice(obstaculosCopy, grafoCopy, j);

            //número ideal de minions para obter o menor tempo
            // if(t < auxT){
            // auxM = j;
            // auxT = t;
            // }

            // // número ideal de minions para ter o melhor aproveitamento no treinamento.
            if (t < auxT) {
                auxM = j;
                auxT = t;
            } else {
                break;
            }
        }
        System.out.println(caso);
        System.out.println("USANDO " + auxM + " || " + "TEMPO: " + auxT);
        System.out.println("=======================================================");
    }
}
