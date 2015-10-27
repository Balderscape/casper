package au.com.clearboxsystems.casper.isopointal;

/**
 * Created by pauls on 21/10/15.
 */
public class IsopointalSetResult {

	public double EAM_A;
	public double EAM_beta;
	public double energyPerAtom;
	public double volume;
	public double density;

	public double startkT;
	public double endkT;
	public int numTrials;
	public double simTime;
	public int attempts;
	public boolean timeoutBeforeMinFound;

	public String isopointalSet;
	public int spaceGroup;
	public double a;
	public double b;
	public double c;
	public double alpha;
	public double beta;
	public double gamma;

	public WyckoffSiteResult[] wyckoffSites;
	public WyckoffPositionResult[] wyckoffPositions;

}
