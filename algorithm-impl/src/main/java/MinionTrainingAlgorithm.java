import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.Integer.parseInt;

public class MinionTrainingAlgorithm implements Serializable {
    private final List<Vertex> obstacles;
    private final Map<Vertex, List<Vertex>> graph;

    public MinionTrainingAlgorithm() {
        obstacles = new ArrayList<>();
        graph = new HashMap<>();
    }

    public void readFile(String filename) {
        final var filepath = Paths.get("src/main/resources/" + filename);
        try (BufferedReader reader = Files.newBufferedReader(filepath, Charset.defaultCharset())) {
            String line;
            while ((line = reader.readLine()) != null) {
                final var word = line.split(" -> ");
                final var a = word[0].split("_");
                final var b = word[1].split("_");
                this.addObstacle(a[0], parseInt(a[1]), 0);
                this.addObstacle(b[0], parseInt(b[1]), 0);
                this.addDependencie(b[0]);
                this.addEdge(a[0], b[0]);
            }
        } catch (IOException e) {
            System.err.println("cannot read file " + e);
        }
    }

    public int practice(int workers) {
        final var baseVertex = new Vertex("root", 0, 0);

        this.obstacles.stream()
                .filter(o -> o.getDependencies() == 0)
                .forEach(o -> {
                    Count.count++;
                    graph.computeIfAbsent(baseVertex, k -> new ArrayList<>());
                    graph.get(baseVertex).add(o);
                    o.setDependencies(o.getDependencies() + 1);
                });

        List<Vertex> working = new ArrayList<>();
        working.add(baseVertex);

        List<Vertex> avaliable = new ArrayList<>();

        int time = 0;
        while (!avaliable.isEmpty() || !working.isEmpty()) {
            Count.count++;
            final var shortestObstacle = working.stream()
                    .min(Comparator.comparing(Vertex::getTime))
                    .orElseThrow(NoSuchElementException::new);

            working.remove(shortestObstacle);
            time += shortestObstacle.getTime();

            final var flag = new AtomicBoolean(true);

            working.forEach(i -> {
                Count.count++;
                i.setTime(i.getTime() - shortestObstacle.getTime());
                if (i.getTime() <= 0)
                    flag.set(false);
            });

            if (graph.get(shortestObstacle) != null) {
                obstacles.stream()
                        .filter(i -> graph.get(shortestObstacle).contains(i))
                        .peek(i -> {
                            i.setDependencies(i.getDependencies() - 1);
                            Count.count++;
                        })
                        .filter(i -> i.getDependencies() == 0)
                        .forEach(avaliable::add);
            }

            avaliable.sort(Comparator.comparing(Vertex::getLabel));

            while (working.size() < workers && !avaliable.isEmpty() && flag.getAcquire()) {
                final var firstAvaliable = avaliable.get(0);
                working.add(firstAvaliable);
                avaliable.remove(firstAvaliable);
            }
        }
        return time;
    }

    public void addObstacle(String label, int time, int dependencies) {

        final var notPresent = obstacles
                .stream()
                .peek(i -> Count.count++)
                .noneMatch(i -> i.getLabel().equals(label));

        if (notPresent) {
            obstacles.add(new Vertex(label, time, dependencies));
            Count.count++;
        }
    }

    private void addDependencie(String label) {
        obstacles.stream()
                .filter(i -> i.getLabel().equals(label))
                .peek(i -> Count.count++)
                .findFirst()
                .ifPresent(i -> i.setDependencies(i.getDependencies() + 1));
    }

    private void addEdge(String labelA, String labelB) {
        Supplier<RuntimeException> notPresentException = () -> new RuntimeException("vertex not present");

        Function<String, Vertex> findVertex = i -> obstacles.stream()
                .filter(j -> j.getLabel().equals(i))
                .peek(k -> Count.count++)
                .findFirst()
                .orElseThrow(notPresentException);

        final var a = findVertex.apply(labelA);
        final var b = findVertex.apply(labelB);

        graph.computeIfAbsent(a, k -> new ArrayList<>());
        graph.get(a).add(b);
    }
}
