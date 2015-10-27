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



	// 1. Create list of equal best structures.
	// 2. Sort by :
	// a. lowest number of wyckoff sites
	// b. choose space group with highest general multiplicity (not multiplicity of chosen wyckoff site)
	// c. choose structure with lowest multiplicity on chosen wyckoff site (sum if multiple sites)
	// d. choose lowest site index (i.e. a beats b)
}
