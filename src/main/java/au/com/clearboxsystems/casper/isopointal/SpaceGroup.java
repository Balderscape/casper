package au.com.clearboxsystems.casper.isopointal;

import au.com.clearboxsystems.casper.math.Variable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pauls on 11/08/15.
 */
public class SpaceGroup {
	public int number;
	public CrystalSystem crystalSystem;
	public String additionalPositionsShortForm;
	public List<WyckoffSite> wyckoffSites = new ArrayList<>();

	@JsonIgnore
	public double a = 1, b = 1, c = 1;

	@JsonIgnore
	public double alpha = Math.PI/2, beta = Math.PI/2, gamma = Math.PI/2;
// if trigonal or hexagonal gamma will be set to 2pi/3 during basis update

	public WyckoffSite getWyckoffSite(String wyckoffSiteLetter) {
		for (WyckoffSite site : wyckoffSites) {
			if(site.code.equalsIgnoreCase(wyckoffSiteLetter))
				return site;
		}
		return null;
	}

	public WyckoffSite getWyckoffSite(int idx) {
        return wyckoffSites.get(idx);
	}

	public int getLinearDegreesOfFreedom() {
		return crystalSystem.linearDegreesOfFreedom;
	}
	public int getAngularDegreesOfFreedom() {
		return crystalSystem.angularDegreesOfFreedom;
	}
    public int getDegreesOfFreedom() {
        return getLinearDegreesOfFreedom() + getAngularDegreesOfFreedom();
    }
    public int getNumWyckoffSites() { return wyckoffSites.size(); }
    public int getNumConstrainedWyckoffSites() {
        int result = 0;
        for (WyckoffSite site : wyckoffSites)
            if (site.getDegreesOfFreedom() == 0)
                result++;

        return result;
    }

	public void updateBasis(Variable[] basis) {
		a = basis[crystalSystem.getIdxA()].curVal;
		b = basis[crystalSystem.getIdxB()].curVal;
		c = basis[crystalSystem.getIdxC()].curVal;

		int idx = crystalSystem.getIdxAlpha();
		if (idx >= 0)
			alpha = basis[idx].curVal;

		idx = crystalSystem.getIdxBeta();
		if (idx >= 0)
			beta = basis[idx].curVal;

		idx = crystalSystem.getIdxGamma();
		if (idx >= 0)
			gamma = basis[idx].curVal;

		if (crystalSystem == CrystalSystem.Trigonal || crystalSystem == CrystalSystem.Hexagonal)
			gamma = 2.0 * Math.PI / 3.0;

	}

	@Override
	public String toString() {
		return "SpaceGroup{" +
				"crystalSystem=" + crystalSystem +
				", a=" + a +
				", b=" + b +
				", c=" + c +
				", alpha=" + alpha +
				", beta=" + beta +
				", gamma=" + gamma +
				'}';
	}
}
