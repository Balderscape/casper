package au.com.clearboxsystems.casper.isopointal;

/**
 * Created by pauls on 4/10/15.
 */
public class EnergyResult {
    public String isopointalSet;
    public double energy;

    public EnergyResult() {
    }

    public EnergyResult(String isopointalSet, double energy) {
        this.isopointalSet = isopointalSet;
        this.energy = energy;
    }
}
