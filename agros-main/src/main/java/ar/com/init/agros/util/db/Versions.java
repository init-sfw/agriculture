package ar.com.init.agros.util.db;

/**
 *
 * @author gmatheu
 */
public enum Versions {

    V1_0("1.0"),
    V1_2("1.2");
    private String id;

    private Versions(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
