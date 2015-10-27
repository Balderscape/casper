package au.com.clearboxsystems.casper.isopointal;

/**
 * Created by pauls on 4/10/15.
 */
public class EnergyResult implements Comparable<EnergyResult> {
    public String isopointalSet;
    public double energy;
    public boolean minFound;
    public double density;

    public EnergyResult() {
    }

    public EnergyResult(String isopointalSet, double energy, boolean minFound, double density) {
        this.isopointalSet = isopointalSet;
        this.energy = energy;
        this.minFound = minFound;
        this.density = density;
    }

    @Override
    public int compareTo(EnergyResult o) {
        return Double.compare(energy, o.energy);
    }
}
