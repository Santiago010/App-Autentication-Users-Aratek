package cn.com.aratek.demo.featureslist;

public class Fingerprint {
    private String name;
    private Number id;

    public Fingerprint(String name, Number id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Number getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Number id) {
        this.id = id;
    }
}