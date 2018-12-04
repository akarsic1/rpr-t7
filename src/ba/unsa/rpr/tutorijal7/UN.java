package ba.unsa.rpr.tutorijal7;

import java.io.Serializable;
import java.util.ArrayList;

public class UN implements Serializable {
    private ArrayList<Drzava> clanice;

    public UN (){
        clanice = null;
    }

    public ArrayList<Drzava> getClanice() {
        return clanice;
    }

    public void setClanice(ArrayList<Drzava> clanice) {
        this.clanice = clanice;
    }

    public UN(ArrayList<Drzava> clanice) {
        this.clanice = clanice;
    }
}
