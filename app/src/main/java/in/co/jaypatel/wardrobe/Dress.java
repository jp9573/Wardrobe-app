package in.co.jaypatel.wardrobe;

import java.io.Serializable;

public class Dress implements Serializable {

    private String cloth;

    public Dress() {

    }

    public String getCloth() {
        return cloth;
    }

    public void setCloth(String cloth) {
        this.cloth = cloth;
    }
}
