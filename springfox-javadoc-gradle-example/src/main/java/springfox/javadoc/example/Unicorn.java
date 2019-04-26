package springfox.javadoc.example;

/**
 * A beautiful unicorn
 */
public class Unicorn {
    /**
     * Unicorn name.
     */
    private String name;
    /**
     * Unicorn magic name (use with care!)
     */
    private String magicName;

    Unicorn(String name, String magicName) {
        this.name = name;
        this.magicName = magicName;
    }

    public Unicorn() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMagicName() {
        return magicName;
    }

    public void setMagicName(String magicName) {
        this.magicName = magicName;
    }
}
