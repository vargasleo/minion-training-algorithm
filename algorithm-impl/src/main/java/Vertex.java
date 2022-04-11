import java.io.Serializable;

public class Vertex implements Comparable<Vertex>, Serializable {
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